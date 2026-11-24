# Mizan v2.0 — Major Release

Welcome to **Mizan v2.0**! This milestone release introduces a complete prayer notification engine, a refined onboarding wizard, customizable navigation transitions, classic bottom navigation, and critical performance optimizations.

---

### What's New & Highlights

#### 🕌 Reliable Prayer Time Alerts & Notifications
- **Automated Alarm Scheduling**: Background `AlarmManager` integration ensures notifications fire accurately for Fajr, Dhuhr, Asr, Maghrib, and Isha, even during deep device sleep.
- **Boot Persistence**: `BootReceiver` automatically restores all prayer reminder alarms when your phone restarts or the app updates.
- **Customizable Advance Notice**: Choose when to be notified: at exact prayer time, or 5, 10, 15, or 30 minutes in advance.
- **Test Notification Tool**: Instant "Send Test Notification" card in *Clock & Timer* settings to verify notifications on your device with one tap.

#### 🚀 Permissions Onboarding Wizard
- **Guided Setup Screen**: Explains why location, exact alarms, and notification permissions are needed for accurate prayer times, Qibla compass calibration, and adhan reminders.
- **Sequential Prompting**: Tapping *Continue* walks through each required system permission dialog sequentially before entering the app.
- Accessible at any time from *Settings → App Permissions*.

#### 🎨 Elements & Feel Customization
- **Window Transition Styles**: Choose your preferred screen change animation in *Elements & Feel*:
  - **Slide & Axis (Default)**: Dynamic horizontal slide with depth elevation.
  - **Pop & Scale**: Playful center zoom expansion using spring dynamics.
  - **Fade & Disappear**: Soft, distraction-free crossfade.
  - **Vertical Rise**: Smooth upward sheet transition.
- **Classic Bottom Navigation Bar**: Toggle docked Material 3 bottom navigation instead of the floating glass bar.
- **Configurable Continue Pill**: The floating "Continue" reading pill is now toggled off by default for a minimal homepage, easily re-enabled anytime.

#### ☕ Support & Community
- **Buy Me a Coffee**: Added official support link for **@oi.hotaro** (`https://buymeacoffee.com/oi.hotaro`).
- **Streamlined Support Drawer**: Cleaned up icons and added direct UPI payment details.

---

### 🛠️ Bug Fixes & Improvements
- **Resolved Hardware Crash (SIGSEGV)**: Fixed native RenderThread crash caused by recursive blur effects on backdrop layers.
- **Startup Flash Elimination**: Theme and settings are now pre-loaded synchronously at splash screen to prevent jarring visual jumps.
- **Real GitHub Releases**: Changelog screen now fetches live version history and release notes directly from GitHub.
- **Appearance Simplification**: Cleaned up redundant typography options from Settings.

---

### 📦 APK Asset
- `Mizan-v2.0.apk` (Signed Release Build, Version Code 5, Version Name 2.0)
