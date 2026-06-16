<div align="center">

# SysHackBar

### `>_` It's not just a name, it's a brand!

**A full-featured Android penetration-testing browser & web-recon toolkit — inspired by DH HackBar.**

Built for security researchers, bug-bounty hunters and CTF players who want a portable offensive-security workbench in their pocket.

![Platform](https://img.shields.io/badge/platform-Android%207.0%2B-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![Language](https://img.shields.io/badge/Kotlin-100%25-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)
![UI](https://img.shields.io/badge/Jetpack%20Compose-Material%203-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white)
![License](https://img.shields.io/badge/license-Educational-FFB300?style=for-the-badge)

**Made by Team SystemAdminBD**

</div>

---

## Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Web Recon Tools](#web-recon-tools)
- [Screenshots](#screenshots)
- [Tech Stack](#tech-stack)
- [Architecture](#architecture)
- [Getting Started](#getting-started)
- [Usage](#usage)
- [Legal & Ethical Disclaimer](#legal--ethical-disclaimer)
- [Roadmap](#roadmap)
- [Contributing](#contributing)
- [Credits](#credits)

---

## Overview

**SysHackBar** turns your Android device into a lightweight web-application security testing console. At its core is a full-screen embedded browser with an integrated payload-injection toolbar, paired with a suite of live reconnaissance tools, encoders, hash utilities and a custom payload library — all wrapped in a dark, terminal-inspired "hacker" interface.

The app opens directly on a branded full-screen browser. Every tool operates against the target you explicitly enter, so you stay in control of what gets tested.

> **For authorized security testing and education only.** See the [disclaimer](#legal--ethical-disclaimer).

---

## Features

### Browser & Injection
- **Full-screen embedded browser** with a custom SysHackBar landing page (no Google start page).
- **Payload-injection toolbar** with horizontally scrollable rows of one-tap payloads: Union/Error/Blind/Double-query SQLi, Auth Bypass, Order-By Bypass, WAF Bypass, DIOS, XSS, LFI, RFI, RCE and more.
- **Live browser tools** that act on the loaded page:
  - **Cookie Editor** — view, set and clear cookies for the current site.
  - **JS Console** — run arbitrary JavaScript on the live page and read the result.
  - **User-Agent Switcher** — Android / iPhone / Desktop / Googlebot presets or a custom UA.
  - **View Source**, reload, undo/redo navigation, home.

### Payloads & Utilities
- **Payload Library** — categorized SQLi, XSS, LFI, RFI and RCE payload sets, copy-to-clipboard ready.
- **My Payloads** — save, edit and reuse your own custom payloads.
- **Encoder / Decoder** — Base64, URL, Hex, Binary and ASCII.
- **Hash Generator & Decrypter** — MD5, SHA-1/256/512 with built-in identification & wordlist cracking.
- **Tamper Data** — send custom request headers and inspect the raw response.
- **String Replacer** — quick find-and-replace transforms for payload crafting.

### Design
- Dark, terminal/matrix aesthetic with neon-green, cyan, violet and amber accents.
- Monospace typography, animated splash, mandatory disclaimer gate.
- Fully responsive layouts that scale across phone sizes.

---

## Web Recon Tools

All recon tools run live against the target you provide and return copy-tappable results.

| Tool | Description |
|------|-------------|
| **Reverse IP Lookup** | Find other domains hosted on the same server/IP. |
| **Subdomain Finder** | Enumerate subdomains and hosts of a domain. |
| **DNS Lookup** | Resolve A / MX / NS / TXT and other DNS records. |
| **Whois Lookup** | Registrar, owner and domain-registration data. |
| **HTTP Headers Inspector** | Fetch and inspect the full HTTP response header set. |
| **Port Scanner** | Scan 17 common TCP ports (FTP, SSH, HTTP, MySQL, RDP, MongoDB…) and report open services. |
| **Admin Panel Finder** | Probe common admin-panel paths and report reachable ones. |
| **SQLi Scanner** | Error-based probe that injects a quote into a URL parameter and matches DB-error signatures. |

---

## Screenshots

> _Add screenshots to a `docs/` or `screenshots/` folder and reference them here._

```
| Browser & Toolbar | Web Recon Tools | Encoder / Hash |
|:---:|:---:|:---:|
| screenshots/browser.png | screenshots/recon.png | screenshots/encoder.png |
```

---

## Tech Stack

- **Language:** Kotlin (100%)
- **UI:** Jetpack Compose + Material 3
- **Navigation:** Compose Navigation (`NavHost`)
- **Async:** Kotlin Coroutines + `StateFlow`
- **Networking:** `HttpURLConnection` / raw TCP sockets for port scanning
- **Min SDK:** 24 (Android 7.0) · **Compile SDK:** 35
- **OSINT endpoints:** public HackerTarget APIs for recon lookups

---

## Architecture

The project follows an MVVM-style structure with a clear separation between data services, view models and Compose screens.

```
android/app/src/main/java/com/systemadminbd/syshackbar/
├── MainActivity.kt
├── data/
│   ├── WebToolsService.kt      # Live recon: reverse IP, DNS, whois, headers, port scan, SQLi…
│   ├── WebToolsViewModel.kt    # State + coroutine orchestration for recon tools
│   ├── Payloads.kt             # Built-in payload library
│   ├── CustomPayloadStore.kt   # Persisted user payloads
│   ├── HashTools.kt            # Hash identify + crack
│   └── SettingsStore.kt
└── ui/
    ├── navigation/AppNavigation.kt
    ├── theme/Theme.kt
    ├── components/Components.kt
    └── screens/                # Browser, WebTools, Encoder, Hash, Tamper, etc.
```

---

## Getting Started

### Prerequisites
- Android Studio (latest stable)
- JDK 17
- Android SDK 35

### Build & Run

```bash
# Clone
git clone <your-repo-url>
cd <repo>/android

# Build a debug APK
./gradlew assembleDebug

# Or install directly to a connected device/emulator
./gradlew installDebug
```

The signed/release APK can be produced with:

```bash
./gradlew assembleRelease
```

The output APK is written to `android/app/build/outputs/apk/`.

---

## Usage

1. Launch the app and accept the disclaimer.
2. You land on the full-screen browser — enter a URL or use the home page.
3. Tap any payload button in the toolbar to inject it into the current page/parameter.
4. Open **Web Tools** for live recon (DNS, whois, headers, port scan, subdomains…).
5. Use the **Encoder**, **Hash** and **Tamper** screens for payload crafting and inspection.
6. Save your favorite payloads under **My Payloads**.

---

## Legal & Ethical Disclaimer

> [!WARNING]
> **SysHackBar is intended strictly for authorized security testing, research and education.**
>
> - Only test systems that you **own** or have **explicit written permission** to assess.
> - Unauthorized access to computer systems is illegal in most jurisdictions and may carry severe penalties.
> - The authors and contributors accept **no liability** for misuse or any damage caused by this tool.
>
> By using this software you agree that you are solely responsible for your actions and that you comply with all applicable laws.

---

## Roadmap

- [ ] SSTI / SSRF / CSRF payload categories
- [ ] Command Injection & NoSQL Injection sets
- [ ] Header Injection (Host header, X-Forwarded-For)
- [ ] Find-in-page & visited-URL history
- [ ] HTTP response body viewer
- [ ] Export recon results to file

---

## Contributing

Contributions, issues and feature requests are welcome. Please open an issue to discuss major changes before submitting a pull request, and keep additions focused on authorized, ethical security testing.

---

## Credits

<div align="center">

**Made with `>_` by Team SystemAdminBD**

_It's not just a name, it's a brand!_

</div>
