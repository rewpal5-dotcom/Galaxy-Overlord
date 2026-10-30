# Galaxy Overlord — Android APK (v2)

Space shooter with profile, stats, leaderboard aur phone-to-phone connect. Poora game APK ke andar hai — **internet ke bina chalta hai, koi ad nahi**.

**Direct APK download:** https://github.com/rewpal5-dotcom/Galaxy-Overlord/releases/latest/download/app-debug.apk

## Naya kya hai (v2)

- **Profile** — naam + auto UID (`GO-XXXXXX`), avatar, best score, games played
- **Stats** — games, best score, total flight time, average, last score, bosses faced
- **Leaderboard** — device ke sabhi profiles ka score; connect karke doosre phone ka score bhi jud jaata hai
- **Connect (beta)** — WebRTC data channel se do phone jodna: Host invite code banata hai, doosra Join karke reply code deta hai. Wi-Fi hotspot ya Bluetooth tethering par kaam karta hai, koi server nahi
- **Settings** — theme (Dark / Black OLED / Neon / Gold), sound toggle, haptics, reset data
- **Top bar** — game ka naam upar, profile chip, menu
- **Pause** — menu khulte hi game rukta hai
- Full-screen immersive, screen-on, zoom lock, back par exit confirm

## Purana gameplay

Drag-to-fly (touch/mouse) + arrows/WASD, auto-fire cannons, enemies, health packs, powerups (clock/gun/shield/bomb), slow-mo chrono, 3-minute boss run, score + high score.

## Build

```bash
./gradlew assembleDebug
# app/build/outputs/apk/debug/app-debug.apk
```

GitHub Actions har push par APK bana kar release mein daal deta hai.

## Note

- Debug-signed APK — direct install ke liye.
- Connect feature ko do asli phones par test karna baaki hai (sandbox mein WebRTC candidates nahi bante). Offline score compare karne ke liye Stats tab mein "Score card copy" hai.
- Real-time saath khelne (versus/co-op) ka netcode agle version mein.

## Tech

Java · WebView + bundled HTML5 canvas game · minSdk 24 · targetSdk 35 · compileSdk 36 · AGP 8.9.3 · Gradle 8.11.1 · koi third-party SDK nahi

## License

MIT
