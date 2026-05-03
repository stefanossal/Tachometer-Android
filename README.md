# Tachometer Android

## Overview

Android Tachometer App is a mobile application designed to monitor a user's real-time speed using GPS data. The app provides speed tracking, customizable speed limits, speed violation logging, map visualization and voice recognition for easier navigation.

This project was developed as an academic assignment focusing on Android development, GPS services, SQLite database integration, SharedPreferences, Google Maps integration and speech technologies.

---

## Features

* **Real-Time Speed Monitoring**

  * Displays the device's current speed using GPS updates.
  * Start and Stop buttons control speed tracking.

* **Custom Speed Limit Settings**

  * Default speed limit is set to **80 km/h**.
  * Users can customize the speed threshold in the Settings screen.
  * Speed limit is stored using SharedPreferences.

* **Speed Violation Alerts**

  * When the user exceeds the speed limit:

    * AlertDialog warning message is displayed.
    * Voice warning is announced using Text-to-Speech.

* **Violation Logs**

  * Stores speed violations in a local SQLite database.
  * Each log includes:

    * Latitude
    * Longitude
    * Speed
    * Timestamp

* **Map Integration**

  * Displays all speed violation records as markers on Google Maps.
  * Each marker shows the date and time of the recorded violation.

* **Voice Recognition**

  * Users can navigate to:

    * Logs
    * Map
    * Settings
  * Speech commands provide hands-free control.

---

## Technologies Used

* **Java**
* **Android SDK**
* **GPS Location Services**
* **SQLite Database**
* **SharedPreferences**
* **Google Maps API**
* **Speech Recognition API**
* **Text-to-Speech Engine**

---

## Installation

1. Clone the repository:

```bash
git clone https://github.com/stefanossal/tachometer-android.git
```

2. Open the project in Android Studio.

3. Add your Google Maps API key if required.

4. Build and run on an Android device with GPS enabled.

---

## Screenshots

<p align="left"> 
  <img src="https://github.com/user-attachments/assets/93ac59e8-c253-42fd-9a01-ddb37d5b56bf" width="25%" /> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
  <img src="https://github.com/user-attachments/assets/462a820e-71bb-4294-92ac-1f1a91768be0" width="25%" /> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
  <img src="https://github.com/user-attachments/assets/8b856106-eeb5-4db1-ad7d-a996f81f1df1" width="25%" />
</p>


