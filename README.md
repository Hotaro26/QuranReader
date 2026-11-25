# Quran Reader

Quran Reader is a modern, feature-rich Android application built with Jetpack Compose, designed to provide a seamless and beautiful experience for reading the Holy Quran. The app focuses on clean design, performance, and user customization.

##  Features

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

 > [!TIP]
> Use **translation** feature to change ayah/Surah language. It supports almost 400+ local lang.

## Screenshots
## Screenshots

| | | |
|:---:|:---:|:---:|
| <img src="https://github.com/user-attachments/assets/ea1ff03f-bff6-4a00-b300-703b78b6612f" width="250" alt="Screenshot 1" /> | <img src="https://github.com/user-attachments/assets/06019d20-c761-4f48-a504-3049b4aa0af7" width="250" alt="Screenshot 2" /> | <img src="https://github.com/user-attachments/assets/c1712c7f-48a6-4ff9-bb93-b71075cef89d" width="250" alt="Screenshot 3" /> |
| <img src="https://github.com/user-attachments/assets/d7e29773-fc8e-4595-b9d0-d1bdb0aaad0d" width="250" alt="Screenshot 4" /> | <img src="https://github.com/user-attachments/assets/837d8b7d-f6d0-4add-a76c-cd24310ca02d" width="250" alt="Screenshot 5" /> | <img src="https://github.com/user-attachments/assets/1cabfe44-10b9-4ea9-950b-03c4e4b5277d" width="250" alt="Screenshot 6" /> |
| <img src="https://github.com/user-attachments/assets/7c15ceac-4503-4875-a574-b301fcaaa80d" width="250" alt="Screenshot 7" /> | <img src="https://github.com/user-attachments/assets/940f7c16-9c19-4f91-977d-52dad2fdfff7" width="250" alt="Screenshot 8" /> | <img src="https://github.com/user-attachments/assets/a8b63965-d03f-4206-ac93-f62df98df404" width="250" alt="Screenshot 9" /> |
<table>
  <tr>
    <td align="center" width="33%">
      <img src="https://github.com/user-attachments/assets/cbe4474e-1c27-4ce4-b262-78aafa03ebca" width="100%" alt="Screenshot 1"/>
    </td>
    <td align="center" width="33%">
      <img src="https://github.com/user-attachments/assets/914bb42f-31ef-4c9c-9017-6041d987832c" width="100%" alt="Screenshot 2"/>
    </td>
    <td align="center" width="33%">
      <img src="https://github.com/user-attachments/assets/f92f3c85-5267-491f-8c59-dc70d5439f39" width="100%" alt="Screenshot 3"/>
    </td>
  </tr>
  <tr>
    <td align="center" width="33%">
      <img src="https://github.com/user-attachments/assets/6c2de71f-f90e-4cff-8ec0-2c2dfa443ce6" width="100%" alt="Screenshot 4"/>
    </td>
    <td align="center" width="33%">
      <img src="https://github.com/user-attachments/assets/366eed50-d2c9-485f-8990-3d4697cadf6e" width="100%" alt="Screenshot 5"/>
    </td>
    <td align="center" width="33%">
      <img src="https://github.com/user-attachments/assets/bfe2013d-d1e5-4dd9-b29c-618759398333" width="100%" alt="Screenshot 6"/>
    </td>
  </tr>
</table>

##  Tech Stack

- **Language:** [Kotlin](https://kotlinlang.org/)
- **UI Framework:** [Jetpack Compose](https://developer.android.com/jetpack/compose)
- **Dependency Injection:** [Hilt](https://developer.android.com/training/dependency-injection/hilt-android)
- **Networking:** [Retrofit](https://square.github.io/retrofit/) & [Gson](https://github.com/google/gson)
- **Database:** [Room](https://developer.android.com/training/data-storage/room)
- **Data Persistence:** [Jetpack DataStore](https://developer.android.com/topic/libraries/architecture/datastore) (Preferences)
- **Architecture:** MVVM (Model-View-ViewModel) with a Repository pattern.
- **Asynchronous Programming:** [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html) & [Flow](https://kotlinlang.org/docs/flow.html)

##  API Source

The application uses the **[Quran API](https://github.com/fawazahmed0/quran-api)** by Fawaz Ahmed.
- **Base URL:** `https://raw.githubusercontent.com/fawazahmed0/quran-api/1/`
- This API provides access to the Quran text in various languages and editions without requiring an API key.

##  Architecture Overview

The project follows the standard Android Architecture Guidelines:
- **Data Layer:** Handles API calls (Retrofit) and local database operations (Room/DataStore).
- **Domain Layer:** (Implicitly managed via Repositories) contains the business logic and models.
- **UI Layer:** Composable screens and ViewModels that manage state using `StateFlow`.

##  Building the Project

1. Clone the repository:
   ```bash
   git clone https://github.com/Hotaro26/QuranReader.git
   ```
2. Open the project in **Android Studio (Iguana or newer)**.
3. Build the project using Gradle:
   ```bash
   ./gradlew assembleDebug
   ```

##  License

This project is open-source. Please attribute the API source (Fawaz Ahmed's Quran API) when using or forking.

## Star History

<a href="https://www.star-history.com/?repos=Hotaro26%2FQuranReader&type=timeline&legend=top-left">
 <picture>
   <source media="(prefers-color-scheme: dark)" srcset="https://api.star-history.com/chart?repos=Hotaro26/QuranReader&type=timeline&theme=dark&legend=top-left&sealed_token=VwcmJAjXTP7ISlVMdvz30JoTFXDFBrcGxobtwBEuZ0TExY_Vu_5ZwGe17o4bnmKCZCcleSBZrD-qIxTDRgX9XNmIikg4pazlh4XFUo6Wcm0PWkdANBTUlDkbuTnGHaq55Wr3K0pSy4EV62Y3tpXHP6Q33E4DI_qoEWVRQaoEm6OEf6eeESVzVyfvJULp" />
   <source media="(prefers-color-scheme: light)" srcset="https://api.star-history.com/chart?repos=Hotaro26/QuranReader&type=timeline&legend=top-left&sealed_token=VwcmJAjXTP7ISlVMdvz30JoTFXDFBrcGxobtwBEuZ0TExY_Vu_5ZwGe17o4bnmKCZCcleSBZrD-qIxTDRgX9XNmIikg4pazlh4XFUo6Wcm0PWkdANBTUlDkbuTnGHaq55Wr3K0pSy4EV62Y3tpXHP6Q33E4DI_qoEWVRQaoEm6OEf6eeESVzVyfvJULp" />
   <img alt="Star History Chart" src="https://api.star-history.com/chart?repos=Hotaro26/QuranReader&type=timeline&legend=top-left&sealed_token=VwcmJAjXTP7ISlVMdvz30JoTFXDFBrcGxobtwBEuZ0TExY_Vu_5ZwGe17o4bnmKCZCcleSBZrD-qIxTDRgX9XNmIikg4pazlh4XFUo6Wcm0PWkdANBTUlDkbuTnGHaq55Wr3K0pSy4EV62Y3tpXHP6Q33E4DI_qoEWVRQaoEm6OEf6eeESVzVyfvJULp" />
 </picture>
</a>
