Safe Security System with STM32 and ESP8266

This project involves integrating an STM32 microcontroller with an ultrasonic sensor and OLED display, and an ESP8266 module with a keypad, LCD display, and a buzzer. All interactions are logged and monitored via Firebase, and alerts are displayed on an Android application.
Features
Object Detection:
Ultrasonic sensor measures distance in cm and triggers an alert if the distance is less than 10 cm.
Password Validation: ESP8266 manages user input through a keypad and validates the entered password.
Emergency Alerts: 
If an incorrect password is entered more than 3 times, the buzzer rings, and an alert is logged.
Cloud Integration:
Logs all events to Firebase for real-time monitoring.
Android App Monitoring:
Displays alerts and logs recent activity.
Components Required

•	STM32 microcontroller
•	ESP8266 Wi-Fi module
•	Ultrasonic sensor (e.g., HC-SR04)
•	OLED display
•	Keypad
•	LCD display
•	Buzzer

Software Requirements

•	STM32CubeIDE for STM32 programming
•	Arduino IDE for ESP8266 programming
•	Firebase Console for database management
•	Android Studio for mobile application development

Steps to Build the System

1.	STM32 Setup
   
Connect the ultrasonic sensor and OLED display to the STM32 microcontroller.
Write code to:
Measure distance using the ultrasonic sensor.
Display the distance in cm on the OLED.
Send an alert message (via UART) to ESP8266 if the distance is less than 10 cm.
Test the functionality.

2.	ESP8266 Setup
   
Connect the keypad, LCD display, and buzzer to ESP8266.
Write code to:
Display "Enter Password" on the LCD.
Validate user-entered password using the keypad.
If the password is correct, display "Correct Password, Safe Unlocked" on the LCD and set password_correct to 1 in Firebase.
If the password is incorrect:
Display "Wrong Password" on the LCD.
Increment a counter for failed attempts.
Ring the buzzer and set emergency_alert to 1 in Firebase after 3 failed attempts.
Log all events to Firebase (object_too_close, password_correct, emergency_alert).
Test the functionality.

3.	Firebase Integration
   
Set up a Firebase project and create the following variables in the database:
object_too_close: Set to 1 when STM32 sends an alert.
password_correct: Set to 1 when a correct password is entered.
emergency_alert: Set to 1 when 3 incorrect password attempts occur.
Write code in ESP8266 to update Firebase variables based on system events.
Test real-time updates on Firebase.

4.	Android Application
   
Develop an Android app to:
Connect to Firebase and read object_too_close, password_correct, and emergency_alert variables.
Display:
"Someone reached near safe" when object_too_close is 1, along with the past 10 records (time and date).
"Safe Unlocked" when password_correct is 1, along with the past 5 records (time and date).
"Emergency Alert" when emergency_alert is 1, along with the past 5 records (time and date).
Test the app with Firebase integration.

