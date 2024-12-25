#include <ESP8266WiFi.h>
#include <FirebaseESP8266.h>
#include <Wire.h>
#include <LiquidCrystal_I2C.h>
#include <Keypad.h>
#include <SoftwareSerial.h>

// Constants
const byte LCD_ADDRESS = 0x27;
const byte LCD_COLS = 16;
const byte LCD_ROWS = 2;
const byte KEYPAD_ROWS = 2;
const byte KEYPAD_COLS = 2;
const byte MAX_PASSWORD_LENGTH = 4;
const byte MAX_ATTEMPTS = 3;
const unsigned long BUZZER_DURATION = 5000;
const unsigned long MESSAGE_DURATION = 2000;

// Pin definitions
const byte BUZZER_PIN = 16;  // GPIO16 (D0)

// Keypad row and column pin assignments
byte ROW_PINS[KEYPAD_ROWS] = {0, 2};     // GPIO0(D3), GPIO2(D4)
byte COL_PINS[KEYPAD_COLS] = {13, 15};   // GPIO13(D7), GPIO15(D8)

// Keypad layout
char KEYS[KEYPAD_ROWS][KEYPAD_COLS] = {
    {'1', '2'},
    {'4', '5'}
};

// Wi-Fi and Firebase configuration
const char* WIFI_SSID = "";
const char* WIFI_PASSWORD = "";
const char* FIREBASE_HOST = "";
const char* FIREBASE_AUTH = "";  
// Firebase objects
FirebaseData firebaseData;
FirebaseConfig firebaseConfig;
FirebaseAuth firebaseAuth;

// SoftwareSerial setup
#define RX_PIN 14  // D5
#define TX_PIN 12  // D6
SoftwareSerial mySerial(RX_PIN, TX_PIN);

// LCD and Keypad objects
LiquidCrystal_I2C lcd(LCD_ADDRESS, LCD_COLS, LCD_ROWS);
Keypad keypad = Keypad(makeKeymap(KEYS), ROW_PINS, COL_PINS, KEYPAD_ROWS, KEYPAD_COLS);

// Security variables
const String CORRECT_PASSWORD = "4545";
String inputPassword = "";
byte attempts = 0;

void setup() {
    // Initialize pins
    pinMode(BUZZER_PIN, OUTPUT);
    digitalWrite(BUZZER_PIN, LOW);

    // Initialize Serial for debugging
    Serial.begin(115200);
    mySerial.begin(115200);

    // Initialize LCD
    Wire.begin();
    lcd.begin();
    lcd.backlight();
    updateDisplay();

    // Connect to Wi-Fi
    connectToWiFi();

    // Firebase configuration
    firebaseConfig.host = FIREBASE_HOST;
    firebaseConfig.signer.tokens.legacy_token = FIREBASE_AUTH;
    Firebase.begin(&firebaseConfig, &firebaseAuth);
    Firebase.reconnectWiFi(true);

    // Initialize Firebase database values
    initializeFirebase();
}

void connectToWiFi() {
    WiFi.mode(WIFI_STA);
    WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
    Serial.print("Connecting to Wi-Fi");
    while (WiFi.status() != WL_CONNECTED) {
        Serial.print(".");
        delay(500);
    }
    Serial.println();
    Serial.print("Connected to Wi-Fi: ");
    Serial.println(WiFi.localIP());
}

void initializeFirebase() {
    if (Firebase.setInt(firebaseData, "/object_too_close", 0)) {
        Serial.println("Initialized object_too_close to 0");
    } else {
        Serial.print("Firebase initialization failed: ");
        Serial.println(firebaseData.errorReason());
    }

    if (Firebase.setInt(firebaseData, "/password_correct", 0)) {
        Serial.println("Initialized password_correct to 0");
    }

    if (Firebase.setInt(firebaseData, "/emergency_alert", 0)) {
        Serial.println("Initialized emergency_alert to 0");
    }
}

void loop() {
    // Check for incoming data from STM32
    if (mySerial.available()) {
        String receivedData = mySerial.readStringUntil('\n');
        handleSTM32Data(receivedData);
    }

    // Check keypad input
    char key = keypad.getKey();
    if (key) {
        handleKeypadInput(key);
    }
}

void handleSTM32Data(String receivedData) {
    if (receivedData.indexOf("ALERT") != -1) {
        Serial.println("Alert received: Object too close!");
        updateFirebase("/object_too_close", 1);
        delay(200);
        updateFirebase("/object_too_close", 0);
    }
}

void handleKeypadInput(char key) {
    Serial.print("\nKey Pressed: ");
    Serial.println(key);

    switch (key) {
        case '2':  // Enter key to check password
            if (inputPassword == CORRECT_PASSWORD) {
                handleCorrectPassword();
            } else {
                handleWrongPassword();
            }
            break;

        case '1':  // Clear input
            clearInput();
            break;

        default:
            if (inputPassword.length() < MAX_PASSWORD_LENGTH) {
                inputPassword += key;
                lcd.setCursor(0, 1);
                lcd.print(inputPassword);  // Show the current input on the LCD
                Serial.print("Current input: ");
                Serial.println(inputPassword);
            }
            break;
    }
}

void handleCorrectPassword() {
    Serial.println("Correct password entered!");
    lcd.clear();
    lcd.print("Correct Password");
    lcd.setCursor(0, 1);
    lcd.print("Safe Unlocked!");
    delay(MESSAGE_DURATION);

    updateFirebase("/password_correct", 1);
    delay(200);
    updateFirebase("/password_correct", 0);

    attempts = 0;
    clearInput();
}

void handleWrongPassword() {
    Serial.println("Wrong password entered!");
    attempts++;
    lcd.clear();
    lcd.print("Wrong Password!");
    delay(MESSAGE_DURATION);

    if (attempts >= MAX_ATTEMPTS) {
        triggerAlarm();
        updateFirebase("/emergency_alert", 1);
        delay(200);
        updateFirebase("/emergency_alert", 0);
        attempts = 0;
    }

    clearInput();
}

void triggerAlarm() {
    Serial.println("Alarm triggered!");
    lcd.clear();
    lcd.print("Alert! Buzzer On");
    digitalWrite(BUZZER_PIN, HIGH);
    delay(BUZZER_DURATION);
    digitalWrite(BUZZER_PIN, LOW);
}

void updateFirebase(const char* path, int value) {
    if (Firebase.setInt(firebaseData, path, value)) {
        Serial.print("Firebase updated: ");
        Serial.print(path);
        Serial.print(" = ");
        Serial.println(value);
    } else {
        Serial.print("Failed to update Firebase: ");
        Serial.println(firebaseData.errorReason());
    }
}

void updateDisplay() {
    lcd.clear();
    lcd.print("Enter Password:");
}

void clearInput() {
    inputPassword = "";
    updateDisplay();
}
