## iliDownloader — Instagram Content Downloader for Android

Beautiful, fast, and reliable Instagram downloader for Android. Built with clean architecture, Kotlin, Material 3, and a Python (Chaquopy) engine under the hood. Supports background downloads with a persistent notification, live in‑app progress, clipboard auto‑paste, and a smooth, modern UI.

### Why clients love it
- **Background downloads that don’t stop**: Close the app and your downloads keep going via a Foreground Service with a rich notification.
- **Real‑time progress, everywhere**: Progress appears both in the notification and inside the app UI.
- **Zero‑tap convenience**: If the input is empty, the app auto‑pastes the latest link in your clipboard.
- **Multiple downloads, stress‑free**: Queue as many as you want; the app handles them sequentially without crashing.
- **Beautiful and accessible**: Material 3 styling, polished animations, light/dark assets, and clear feedback.

---

### Preview
> Replace the placeholders below with your own screenshots/GIFs from `app/release` or device captures.

| Home | Progress | Notification |
|---|---|---|
| ![Home](./docs/images/home.png) | ![Progress](./docs/images/progress.png) | ![Notification](./docs/images/notification.png) |

---

## Features
- **Download by username**: Grabs all public posts and saves them under `/sdcard/InstaLoaderApp/<username>`.
- **Download by post link**: Supports both `https://www.instagram.com/p/...` and `https://www.instagram.com/reel/...` to `/sdcard/InstaLoaderApp/posts`.
- **Foreground Service**: Reliable background execution with Android‑compliant notifications.
- **Live in‑app progress**: Smooth progress bar and status messaging.
- **Clipboard auto‑paste**: One‑tap flow—just copy a link and hit Download.
- **Queueing**: Enqueue multiple downloads without blocking the UI.
- **Language selection screen**: English and Persian supported.

## Tech Stack
- **Android**: Kotlin, Material 3, ViewBinding, Coroutines
- **Architecture**: Clean code principles, single‑responsibility activities, foreground service for background work
- **Python**: [Chaquopy](https://chaquo.com/chaquopy/) to run the `instaloader` library inside the app
- **Libraries**: 
  - `instaloader` (Python) for reliable Instagram media downloading

## How it works (high level)
1. The user enters a username or Instagram link. If empty, the app auto‑pastes from the clipboard.
2. A Foreground Service (`DownloadService`) enqueues and performs downloads in the background.
3. The service updates a persistent notification and broadcasts progress updates.
4. `MainActivity` listens to progress broadcasts and updates the in‑app progress bar.
5. The Python layer (`app/src/main/python/script.py`) uses `instaloader` to fetch media and report progress.

## Permissions
- Storage (MANAGE_EXTERNAL_STORAGE or WRITE/READ depending on API) — to save media to `/sdcard/InstaLoaderApp/`.
- Foreground service — to keep downloads running in the background.
- Notifications (Android 13+) — to display foreground service progress.

The app guides the user through a friendly permission flow via `LanguageSelectionActivity` → `PermissionGrantActivity`.

## Project Structure
```
app/
  src/main/java/com/alphacorp/instaloader/
    MainActivity.kt                # UI and in‑app progress
    DownloadService.kt             # Foreground background downloads + notifications
    LanguageSelectionActivity.kt   # Language bootstrap
    PermissionGrantActivity.kt     # Storage + notification permission flow
  src/main/python/
    script.py                      # instaloader integration and progress API
  src/main/res/                    # Material 3 UI, animations, drawables
```

## Build & Run
### Prerequisites
- Android Studio Iguana or newer
- Android SDK 24+
- Gradle (via wrapper)

### Steps
1. Clone the repository.
2. Open the project in Android Studio.
3. Let Gradle sync and download dependencies (Chaquopy and Python wheels are configured in Gradle).
4. Connect a device (Android 8+) or start an emulator.
5. Run the app.

### Signing & Release
- Generate a release keystore and configure signing in `app/build.gradle` or Android Studio’s Signing Configs.
- Build a release APK/AAB via Build → Generate Signed Bundle/APK.

## Usage
1. Launch the app and choose your language.
2. Grant the requested storage and notification permissions.
3. Paste an Instagram username or post link, or just tap Download to auto‑paste from clipboard.
4. Track progress in the app and in the notification.
5. Queue additional downloads immediately—each new input is added to the background queue.

## Roadmap
- Settings screen (choose download folder, data saver mode)
- Pause/Resume for the download queue
- In‑app gallery with share/delete
- More locales

## Privacy & Legal
- This app downloads only publicly available content. Respect creators’ rights.
- Ensure you have permission to download and use content.
- This project is not affiliated with Instagram.

## Troubleshooting
- “No posts found or account is private”: Only public profiles are supported.
- Notification doesn’t appear (Android 13+): Enable app notifications in system settings.
- Storage errors on Android 11+: Ensure you granted “All files access” when prompted.
- Slow or stuck progress: Network conditions and Instagram rate limits can affect speed. The app shows smooth progress and completes as soon as the Python engine finishes.

## Contributing
Issues and pull requests are welcome. Please read `CONTRIBUTING.md` before submitting changes.

## License
Licensed under the terms of the `LICENSE` file in this repository.

---

### Pitch to Clients
- **Conversion‑focused UX**: Minimal taps, clear feedback, delightful animations.
- **Enterprise‑ready reliability**: Foreground service guarantees downloads continue even if the app is closed.
- **Maintainable code**: Clean Kotlin, separation of concerns, and typed contracts.
- **Extensible**: Add premium features (e.g., batch rules, filters, private API with auth) without changing the core flow.

Want a branded build, custom flows, or additional platforms? Let’s talk.

# InstaLoader - Beautiful Instagram Content Downloader

A luxurious, stylish, and feature-rich Instagram content downloader app with beautiful Material 3 design, smooth animations, and real-time progress tracking.

## ✨ Features

### 🎨 **Luxurious Design**
- **Material 3 Design System** - Modern, beautiful UI following Google's latest design guidelines
- **Gradient Backgrounds** - Beautiful gradient backgrounds with Instagram-inspired colors
- **Card-based Layout** - Elegant card design with proper shadows and elevation
- **Custom Icons** - Beautiful vector icons for all UI elements
- **Responsive Design** - Adapts beautifully to different screen sizes

### 📊 **Progress Tracking**
- **Real-time Progress Bar** - Beautiful gradient progress bar with percentage display
- **Status Updates** - Dynamic status messages showing download progress
- **Progress Animation** - Smooth progress updates with beautiful transitions
- **Download Simulation** - Realistic progress simulation for better user experience

### 🎭 **Beautiful Animations**
- **Entrance Animations** - Staggered entrance animations for all UI elements
- **Progress Animations** - Smooth fade-in/fade-out animations for progress section
- **Button Animations** - Pulse animation for download button and success animations
- **Card Animations** - Beautiful slide-up animations for main content
- **Status Animations** - Smooth alpha transitions for status updates

### 🌙 **Theme Support**
- **Light Theme** - Beautiful light theme with Instagram-inspired colors
- **Dark Theme** - Elegant dark theme with proper contrast and readability
- **Auto Theme Switching** - Automatic theme switching based on system preferences
- **Custom Color Palette** - Luxurious color scheme with proper accessibility

### 🔧 **Technical Features**
- **Kotlin Coroutines** - Asynchronous download operations
- **Material Components** - Latest Material Design components
- **View Binding** - Modern view binding for better performance
- **Clean Architecture** - Well-structured, maintainable code
- **Error Handling** - Comprehensive error handling with user-friendly messages

## 🎨 Design Elements

### Color Palette
- **Primary**: Beautiful orange (#FF6B35)
- **Secondary**: Vibrant yellow (#FFD93D)
- **Accent**: Modern purple (#6C63FF)
- **Instagram Colors**: Pink (#E4405F), Purple (#833AB4), Orange (#FD1D1D), Yellow (#FCAF45)

### Typography
- **App Title**: 32sp, bold, sans-serif-light
- **Section Headers**: 18sp, bold
- **Body Text**: 16sp, regular
- **Captions**: 14sp, regular
- **Progress Text**: 14sp, bold

### Layout Components
- **Main Card**: 24dp corner radius, 16dp elevation
- **Info Cards**: 16dp corner radius, 8dp elevation
- **Input Field**: 16dp corner radius with Instagram icon
- **Download Button**: 20dp corner radius with gradient background
- **Progress Bar**: 12dp corner radius with gradient fill

## 🚀 Getting Started

### Prerequisites
- Android Studio Arctic Fox or later
- Android SDK 21+ (API Level 21)
- Python 3.8+ (for backend functionality)

### Installation
1. Clone the repository
2. Open in Android Studio
3. Sync Gradle files
4. Build and run on device/emulator

### Usage
1. Enter Instagram username or post URL
2. Tap "Download Content"
3. Watch beautiful progress animations
4. Enjoy your downloaded content!

## 🛠️ Technical Implementation

### Architecture
- **MVVM Pattern** - Model-View-ViewModel architecture
- **Repository Pattern** - Clean data management
- **Coroutines** - Asynchronous operations
- **Material 3** - Latest design system

### Key Components
- **MainActivity**: Main UI controller with animations
- **Layout Files**: Beautiful XML layouts with Material Design
- **Drawable Resources**: Custom vector graphics and gradients
- **Animation Files**: Smooth XML animations
- **Theme Files**: Comprehensive theming system

### Dependencies
- Material Design 3
- AndroidX Core KTX
- ConstraintLayout
- Kotlin Coroutines
- Chaquopy (Python integration)

## 🎯 Future Enhancements

- [ ] **Batch Downloads** - Download multiple posts simultaneously
- [ ] **Download History** - Track download history with beautiful UI
- [ ] **Favorites** - Save favorite accounts for quick access
- [ ] **Settings** - Customizable download options
- [ ] **Statistics** - Beautiful download statistics dashboard
- [ ] **Sharing** - Share downloaded content directly from app

## 🤝 Contributing

We welcome contributions! Please see our [Contributing Guidelines](CONTRIBUTING.md) for details.

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- **Instagram** - For the inspiration
- **Material Design Team** - For the beautiful design system
- **Kotlin Team** - For the amazing language
- **Android Community** - For continuous support and feedback

---

**Made with ❤️ and beautiful design principles**

*Transform your Instagram experience with the most beautiful downloader app ever created!*
