# PW#1: Introduction to Jetpack Compose UI & State Management

**University Course:** Mobile Device Software Development  
**Project Theme:** Emergency Power Supply System  

## 📝 Overview
This is the first practical work (PW#1) for the course. It focuses on setting up the fundamental UI architecture and managing state in Jetpack Compose. The goal is to build a basic interactive dashboard for the Emergency Power System.

## ✨ Key Features
- **Navigation:** Setup of the main application structure with `PrimaryTabRow` to seamlessly navigate between different practical works.
- **UI Components:** Creation of modular and reusable Compose UI elements such as `HeaderSection`, `MetricDisplay`, and customized buttons.
- **State Management:** Implementation of Compose state handling (`remember`, `mutableStateOf`) for dynamic UI updates without the need for traditional XML layouts.
- **Interactive Dashboard:** `Practice1Screen` displays mock metrics like **Load Forecast (W)** and **Battery Charge Level (%)**. The values are dynamically regenerated when the user clicks the "Update Forecast" button to visually demonstrate UI recomposition.

## 📂 Code Structure Highlights
- `MainActivity.kt`: Entry point of the application setting up the Compose theme.
- `MainScreen.kt`: Implements the tabbed navigation (PW 1, PW 2, PW 3, PW 4).
- `Practice1Screen.kt`: The main UI for this specific practical work, containing the state logic and layout.
- `ui/components/`: Reusable Compose components used across the screens.

## 🚀 Running the App
1. Open this folder (`PW#1ТВ-31_Фельчин_Микола_Борисович`) in **Android Studio**.
2. Sync the project with Gradle files.
3. Run the `app` configuration on your emulator or physical device.
