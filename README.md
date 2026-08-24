# Agri Cost Tracker (The Modern Agronomist) - Android App

A native Android mobile application implemented in **Kotlin** with **Jetpack Compose**, **Material 3**, and **Room Database**, matching the design system and workflows of the **Agri Cost Tracker** Stitch project.

---

## 📱 Features & Screens

1. **Dashboard (`DashboardScreen.kt`)**:
   - Total Operational Land card (Total Acres, Cultivated vs. Fallow breakdown).
   - Real-time Estimated Spraying Cost card ($ / Acre × acreage calculation with progress indicator).
   - Real-time Estimated Harvesting Cost card ($ / Acre × acreage calculation with projection tags).
   - Seasonal Expenditure Flow Bar Chart (Monthly breakdown from MAR to DEC for Growth & Maintenance).
   - "Generate Full Fiscal Report" modal export action.
   - Quick navigation to review input rates.

2. **Service Rates (`ServiceRatesScreen.kt`)**:
   - Spraying Cost per Acre input (`USD/AC`).
   - Crop Cutting (Harvesting & Threshing) Cost per Acre input (`USD/AC`).
   - Global Trend Indicator (+4.2% vs. last season) and Region information.
   - Live update and persistence with audit integrity preservation.

3. **Farmer Profile / Land Entry (`FarmerProfileScreen.kt`)**:
   - Personal Identity management (Full name).
   - Operational Scale (Total owned land in Acres).
   - GPS-verified active sector display (e.g. *Central Valley Sector 7*).
   - Cryptographic privacy standard assurances.

4. **Activity History (`ActivityHistoryScreen.kt`)**:
   - Seasonal Records archive with active season selector (2024 / 2023).
   - Hero summary cards for Total Seasonal Investment and Pending Payables.
   - Status filtering (`COMPLETED`, `INVOICED`, `ARCHIVED`).
   - Add new seasonal expense/activity dialog.
   - Pre-populated initial seed data matching the design.

---

## 🛠️ Tech Stack & Architecture

- **Language**: Kotlin 1.9+
- **UI Toolkit**: Jetpack Compose with Material 3
- **Architecture**: MVVM + Repository Pattern + Reactive Coroutine Flow
- **Database**: Android Room Persistence Library with SQLite
- **Gradle**: AGP 8.3.0, Gradle 8.4+

---

## 🚀 How to Run in Android Studio

1. Open **Android Studio** (Hedgehog, Iguana, Jellyfish, Koala, Ladybug, or later).
2. Select **Open** and select the `e:\AgroApp` directory.
3. Allow Gradle to sync dependencies.
4. Select an Android Emulator (API 26+) or physical device.
5. Click **Run** (`Shift + F10`).
