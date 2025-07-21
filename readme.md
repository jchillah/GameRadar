# GameRadar 🎮

GameRadar ist eine moderne Android-App zur Suche, Verwaltung und Analyse von Videospielen. Die App
nutzt die RAWG-API und bietet Favoriten, Wunschliste, Filter, Statistiken und viele
Einstellungsmöglichkeiten.

## Highlights ✨

- **Edge-to-Edge-Design:** Inhalte beginnen direkt unter der Statusleiste, keine doppelten AppBars,
  keine verschachtelten Scaffold-Strukturen.
- **Konsistente UI:** Einheitliche Titelzeile mit Actions (Teilen, Favorit, Refresh) auf allen
  Hauptscreens.
- **Dark-/Light-Mode-Umschaltung:** Nutzer können das Design jederzeit in den Einstellungen
  wechseln.
- **Settings-Screen:** Alle App-Einstellungen (Design, Sprache, Bildqualität, Benachrichtigungen,
  etc.) zentral und MVVM-konform.
- **Fehlerbehandlung pro Feld:** Fehler wie „Keine Website verfügbar“ oder „Keine Screenshots
  verfügbar“ werden gezielt im jeweiligen Bereich angezeigt.
- **Null-Safety & Logging:** Alle Datenzugriffe sind null-sicher, Navigation und Datenübergaben
  werden geloggt.
- **Navigation nur mit primitiven Typen:** Es werden ausschließlich IDs oder Strings übergeben,
  keine komplexen Objekte.
- **Offline-First:** Favoriten und Detaildaten werden lokal mit Room gecacht.
- **Moderne Compose-Architektur:** Klare Trennung von UI, State und Logik, State-Hoisting, keine
  UI-Logik im ViewModel.

---

## Screenshots 🏞️

| Suche                           | Favoriten                     | Wunschliste                       | Einstellungen                     |
|---------------------------------|-------------------------------|-----------------------------------|-----------------------------------|
| ![Suche](img/screen1.png)       | ![Favoriten](img/screen2.png) | ![Wunschliste](img/screen3.png)   | ![Einstellungen](img/screen4.png) |
| ![Statistiken](img/screen5.png) | ![Detail](img/screen6.png)    | ![Tablet-Layout](img/screen7.png) | ![Export/Import](img/screen8.png) |

---

## Features ➳

- 🔍 **Spielsuche** nach Titel, Plattform, Genre, Bewertung
- 🏷️ **Filter & Sortierung** (Plattform, Genre, Bewertung, Erscheinungsjahr)
- ⭐ **Favoriten speichern** (Offline mit Room)
- 📝 **Detailseite** mit Beschreibung, Galerie, Entwickler, Plattformen, Metacritic, Spielzeit
- 🆕 **Listenansicht** für Neuerscheinungen & Top-rated
- 📤 **Spiele teilen** via Link
- ⚡ **Offline-Cache** für schnelle Anzeige & Fehlervermeidung
- 🎨 **Jetpack Compose UI** – modern, performant, flexibel
- 🏗️ **MVVM-Architektur** mit sauberem Repository-Pattern
- ⏳ **Ladeindikator & Error-UI** mit Retry-Funktion (zentralisierte Loading-Komponente)
- 🛡️ **Fehlerhinweise direkt im Feld** (z. B. „Keine Screenshots verfügbar“)
- 📝 **Logging** für Navigation und Datenübergabe
- 🌓 **Dark-/Light-Mode** per Schalter im SettingsScreen
- ⚙️ **SettingsScreen**: Sprache, Bildqualität, Benachrichtigungen, Design, u.v.m.

---

## Technischer Aufbau 💻

### Projektstruktur (MVVM + Compose)

```bash
de.syntax_institut.androidabschlussprojekt
├── data
│   ├── local         # Room: Entities, DAOs, DB, Models
│   ├── remote        # Retrofit DTOs + API-Service
│   └── repositories  # Kommunikation zwischen Datenquellen & Settings
├── domain           # Geschäftslogik, Domain-Modelle (z. B. Genre, Platform)
├── di               # Dependency Injection (Koin)
├── navigation       # Jetpack Navigation Komponenten
├── ui
│   ├── components   # Wiederverwendbare Composables (common, detail, search, settings)
│   ├── screens      # Hauptscreens (Search, Detail, Favorites, Settings)
│   ├── states       # UI-State-Modelle
│   ├── theme        # Farben, Typographie, Shapes
│   └── viewmodels   # ViewModel-Logik & UI-State
├── utils            # Hilfsklassen (z. B. Resource.kt, NetworkUtils)
└── services         # z. B. Background-Tasks
```

---

### Datenhaltung & API

- **Favoriten & Detail-Cache:** Offline verfügbar mit Room
- **RAWG Video Games API:**
  - Base URL: `https://api.rawg.io/api/`
  - Endpoints: `/games?search=...`, `/games/{id}`
  - Filter: Plattform, Genre, Bewertung, Datum
  - API-Key in `local.properties` eintragen
    ```bash
    API_KEY=YOUR_API_KEY
    ```

### Frameworks & Libraries

- **Retrofit** + **Moshi** für API
- **Room** für lokale Datenhaltung
- **Jetpack Compose** (UI) + Navigation
- **Coil** für Bild-Loading
- **Accompanist** für Paging & SwipeRefresh
- **Koin** für Dependency Injection
- Optional: **Firebase Crashlytics & Analytics**

---

## Fehlerbehandlung & UX

- **Fehler pro Feld:**  Website oder Screenshots fehlen? → ErrorCard nur im jeweiligen Bereich Echte
  API-/Netzwerkfehler? → Globale ErrorCard mit Retry
- **Null-Safety:**  Alle Datenzugriffe sind null-sicher, keine Crashes durch fehlende Felder
- **Logging:**  Navigation und Datenübergaben werden geloggt (z. B. gameId bei Detailaufruf)
- **Keine komplexen Objekte in Navigation:**  Es werden nur primitive Typen (Int, String, Boolean)
  übergeben

---

## Werbung, Pro-Status & Rewarded Ads

- Die App verwendet Google AdMob für Banner- und Rewarded-Ads.
- **Nicht-Pro-User:** Werbung ist immer aktiviert, Statistiken und Export werden nach dem Ansehen
  einer Rewarded Ad freigeschaltet.
- **Pro-User:** Kann Werbung in den Einstellungen freiwillig aktivieren. Ist Werbung deaktiviert,
  sind Statistiken und Export immer verfügbar. Ist Werbung aktiviert, werden diese Features wie bei
  Nicht-Pro-Usern nach einer Rewarded Ad freigeschaltet.
- Es werden ausschließlich Test-IDs für AdMob verwendet (keine Gefahr für das AdMob-Konto beim
  Testen).

---

## Setup

1. **API Key** erstellen: [RAWG API Docs](https://rawg.io/apidocs)
2. Key in `local.properties` eintragen: `API_KEY=dein_key`
3. Projekt in Android Studio öffnen und ausführen

---

## Ausblick

- [x] Push-Notifikationen zu neuen Top-Spielen
- [x] Dark Mode (umschaltbar)
- [x] Vollbild-Screenshot-Galerie in Detailseite
- [x] Fehlerbehandlung pro Feld (statt global)
- [x] Logging für Navigation und Fehler
- [x] Erweiterte Paging-Unterstützung
- [x] SettingsScreen mit allen wichtigen App-Optionen
- [ ] Firebase-Integration für Sync & Analytics

---

## Lizenz

MIT License – Siehe [LICENSE](LICENSE) für weitere Informationen.
- Android Studio Hedgehog oder neuer
- Projekt klonen
- API_KEY in `local.properties` eintragen: `API_KEY=...any key...`
- Projekt öffnen und auf ein Gerät/Emulator ausführen

