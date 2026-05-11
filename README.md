# Quran Reader

Quran Reader is a modern, feature-rich Android application built with Jetpack Compose, designed to provide a seamless and beautiful experience for reading the Holy Quran. The app focuses on clean design, performance, and user customization.

## 🌟 Features

- **Modern UI:** Built entirely with Jetpack Compose for a smooth and responsive interface.
- **Home Dashboard:** Quick access to your last read position, recent bookmarks, and current prayer times.
- **Comprehensive Surah List:** Easily browse through all 114 Surahs with Arabic and English names.
- **Reading Experience:** 
    - High-quality Arabic Uthmani script.
    - Multiple translation support.
    - Bookmark any Ayah for later reference.
- **Deep Customization:**
    - **Theme Modes:** Support for System, Light, and Dark modes.
    - **Color Palettes:** Choose from multiple beautiful palettes including Material You (Dynamic), Classic Green, Lavender, Pink, Mocha, Catppuccin, and Monochrome.
    - **Translation Selection:** Search and choose from dozens of available translations via the API.
- **Prayer Times:** View prayer times based on your local time.
- **Local Storage:** Your progress and bookmarks are saved locally using Room and DataStore.
## Screenshots

<table>
  <tr>
    <td align="center" width="50%">
      <img src="https://github.com/user-attachments/assets/cbe4474e-1c27-4ce4-b262-78aafa03ebca" width="100%" alt="Screenshot 1"/>
    </td>
    <td align="center" width="50%">
      <img src="https://github.com/user-attachments/assets/914bb42f-31ef-4c9c-9017-6041d987832c" width="100%" alt="Screenshot 2"/>
    </td>
  </tr>
  <tr>
    <td align="center" width="50%">
      <img src="https://github.com/user-attachments/assets/f92f3c85-5267-491f-8c59-dc70d5439f39" width="100%" alt="Screenshot 3"/>
    </td>
    <td align="center" width="50%">
      <img src="https://github.com/user-attachments/assets/6c2de71f-f90e-4cff-8ec0-2c2dfa443ce6" width="100%" alt="Screenshot 4"/>
    </td>
  </tr>
  <tr>
    <td align="center" width="50%">
      <img src="https://github.com/user-attachments/assets/366eed50-d2c9-485f-8990-3d4697cadf6e" width="100%" alt="Screenshot 5"/>
    </td>
    <td align="center" width="50%">
      <img src="https://github.com/user-attachments/assets/bfe2013d-d1e5-4dd9-b29c-618759398333" width="100%" alt="Screenshot 6"/>
    </td>
  </tr>
</table>

## 🛠️ Tech Stack

- **Language:** [Kotlin](https://kotlinlang.org/)
- **UI Framework:** [Jetpack Compose](https://developer.android.com/jetpack/compose)
- **Dependency Injection:** [Hilt](https://developer.android.com/training/dependency-injection/hilt-android)
- **Networking:** [Retrofit](https://square.github.io/retrofit/) & [Gson](https://github.com/google/gson)
- **Database:** [Room](https://developer.android.com/training/data-storage/room)
- **Data Persistence:** [Jetpack DataStore](https://developer.android.com/topic/libraries/architecture/datastore) (Preferences)
- **Architecture:** MVVM (Model-View-ViewModel) with a Repository pattern.
- **Asynchronous Programming:** [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html) & [Flow](https://kotlinlang.org/docs/flow.html)

## 📡 API Source

The application uses the **[Quran API](https://github.com/fawazahmed0/quran-api)** by Fawaz Ahmed.
- **Base URL:** `https://raw.githubusercontent.com/fawazahmed0/quran-api/1/`
- This API provides access to the Quran text in various languages and editions without requiring an API key.

## 🏗️ Architecture Overview

The project follows the standard Android Architecture Guidelines:
- **Data Layer:** Handles API calls (Retrofit) and local database operations (Room/DataStore).
- **Domain Layer:** (Implicitly managed via Repositories) contains the business logic and models.
- **UI Layer:** Composable screens and ViewModels that manage state using `StateFlow`.

## 🚀 Building the Project

1. Clone the repository:
   ```bash
   git clone https://github.com/Hotaro26/QuranReader.git
   ```
2. Open the project in **Android Studio (Iguana or newer)**.
3. Build the project using Gradle:
   ```bash
   ./gradlew assembleDebug
   ```

## 📄 License

This project is open-source. Please attribute the API source (Fawaz Ahmed's Quran API) when using or forking.

---
Developed with ❤️ by [Hotaro](https://github.com/Hotaro26)
