<div align="center">

# 📥 iliDownloader

Beautiful Instagram Content Downloader for Android

[![Platform](https://img.shields.io/badge/platform-Android-brightgreen)](https://developer.android.com)
[![Kotlin](https://img.shields.io/badge/Kotlin-1.9%2B-7F52FF)](https://kotlinlang.org)
[![Material](https://img.shields.io/badge/Material-3-6200EE)](https://m3.material.io/)
[![License](https://img.shields.io/badge/license-MIT-blue.svg)](./LICENSE)

</div>

> Inspired by the clean presentation style of [`OnlineShop`](https://github.com/iliaxp/OnlineShop).

---

## ✨ Highlights

- 🛡️ **Background downloads that don’t stop** — Foreground Service keeps downloads running even if the app closes.
- 📊 **Live progress, everywhere** — Real‑time progress in notification and in‑app UI.
- 📋 **Clipboard auto‑paste** — Empty field? We paste your latest link automatically.
- 🧰 **Multiple downloads** — Queue any number without crashing or blocking.
- 🎨 **Modern UI** — Material 3, smooth animations, and accessible design.

---

## 🖼️ Preview

> Replace placeholders with your screenshots or GIFs.

| Home | Progress | Notification |
|---|---|---|
| ![Home](https://github.com/user-attachments/assets/56590952-4970-4cf8-be2f-4275c3b9a787) | ![Progress](https://github.com/user-attachments/assets/5eafe9cd-9784-4f16-9ae4-ef362673d951) | ![Notification](https://github.com/user-attachments/assets/175cc2cc-b7a4-4bc7-b6a0-17ebcc5c9bf8) |

---


## 🧩 Features

| Category | Details |
|---|---|
| Download by username | Saves public posts to `/sdcard/InstaLoaderApp/<username>` |
| Download by link | Supports `…/p/…` and `…/reel/…` to `/sdcard/InstaLoaderApp/posts` |
| Foreground Service | Reliable background execution with Android‑compliant notifications |
| In‑app progress | Smooth progress bar and status updates |
| Clipboard auto‑paste | One‑tap flow: copy a link, hit Download |
| Queueing | Multiple downloads handled sequentially |
| Localization | English and Persian language selection |

---

## 🛠️ Technology Stack

| Layer | Tech |
|---|---|
| Language | Kotlin |
| UI | Material 3, ViewBinding |
| Concurrency | Coroutines |
| Background | Foreground Service + Notifications |
| Python Engine | [Chaquopy](https://chaquo.com/chaquopy/) |
| Downloader | `instaloader` (Python) |

---

## 🧭 How It Works

1. Input a username or Instagram link (or leave empty to auto‑paste from clipboard).
2. `DownloadService` enqueues and performs background downloads as a Foreground Service.
3. The service updates the persistent notification and broadcasts progress events.
4. `MainActivity` listens for broadcasts and updates the in‑app progress UI.
5. `script.py` (Python) uses `instaloader` to fetch media and report progress.

---

## 🔐 Permissions

- Storage (MANAGE_EXTERNAL_STORAGE or WRITE/READ) — Save media to `/sdcard/InstaLoaderApp/`.
- Foreground Service — Keep downloads running in background.
- Notifications (Android 13+) — Show progress notifications.

The app guides users via `LanguageSelectionActivity` → `PermissionGrantActivity`.

---

## 🗂️ Project Structure

```
app/
  src/main/java/com/alphacorp/instaloader/
    MainActivity.kt                # UI + in‑app progress
    DownloadService.kt             # Foreground downloads + notifications
    LanguageSelectionActivity.kt   # Language bootstrap
    PermissionGrantActivity.kt     # Permission flow
  src/main/python/
    script.py                      # instaloader integration + progress API
  src/main/res/                    # Material 3 UI, animations, drawables
```

---

## 🚀 Getting Started

### Prerequisites

- Android Studio Iguana or newer
- Android SDK 24+
- Gradle (wrapper included)

### Installation

```bash
git clone https://github.com/your-org/iliDownloader.git
cd iliDownloader
```

1) Open in Android Studio and wait for Gradle sync.
2) Connect a device (Android 8+) or start an emulator.
3) Run the app.

### Signing & Release

- Generate a release keystore and configure signing in Android Studio or `app/build.gradle`.
- Build → Generate Signed Bundle/APK to produce AAB/APK.

---

## 📱 Usage

1. Choose language on first launch.
2. Grant storage and notification permissions.
3. Paste a username or post link (or tap Download to auto‑paste from clipboard).
4. Track progress both in‑app and in notification.
5. Add more links immediately; they’ll queue in the background.

---

## 🗺️ Roadmap

- Settings screen (destination folder, data saver)
- Pause/Resume queue
- In‑app gallery with share/delete
- More locales

---

## 🔒 Privacy & Legal

- Downloads only publicly available content. Respect creators’ rights.
- Ensure you have permission to download and use content.
- Not affiliated with Instagram.

---

## 🧰 Troubleshooting

- “No posts found or account is private” → Only public profiles are supported.
- No notification on Android 13+ → Enable app notifications in system settings.
- Storage errors on Android 11+ → Grant “All files access” when prompted.
- Slow/stuck progress → Network/rate limiting can affect speed; the UI remains responsive.

---

## 🤝 Contributing

Contributions are welcome! Please see `CONTRIBUTING.md` and open an issue or PR.

---

## 📄 License

MIT — see `LICENSE` for details.

---

## 💼 For Clients

- Conversion‑focused UX with minimal taps and delightful feedback.
- Enterprise‑grade reliability via Foreground Service.
- Clean, maintainable Kotlin code and clear separation of concerns.
- Extensible for premium features (batch rules, filters, private API with auth).

Need branding, custom flows, or multi‑platform support? Let’s talk.


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
