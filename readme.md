# GameRadar 🎮

**Entdecke deine nächste Gaming-Leidenschaft – modern, schnell, fehlerrobust.**

GameRadar ist die moderne Android-App, um neue Spiele zu entdecken, zu filtern, zu bewerten und
Favoriten zu speichern. Entwickelt mit Jetpack Compose, MVVM, Room und der RAWG-API – für ein
konsistentes, performantes und fehlerfreies Nutzererlebnis.

---

## Highlights

- **Edge-to-Edge-Design:** Inhalte beginnen direkt unter der Statusleiste, keine doppelten AppBars,
  keine verschachtelten Scaffold-Strukturen.
- **Konsistente UI:** Einheitliche Titelzeile mit Actions (Teilen, Favorit, Refresh) auf allen
  Hauptscreens.
- **Fehlerbehandlung pro Feld:** Fehler wie „Keine Website verfügbar“ oder „Keine Screenshots
  verfügbar“ werden gezielt im jeweiligen Bereich angezeigt, nicht mehr als globale ErrorCard.
- **Null-Safety & Logging:** Alle Datenzugriffe sind null-sicher, Navigation und Datenübergaben
  werden geloggt.
- **Navigation nur mit primitiven Typen:** Es werden ausschließlich IDs oder Strings übergeben,
  keine komplexen Objekte.
- **Offline-First:** Favoriten und Detaildaten werden lokal mit Room gecacht.
- **Moderne Compose-Architektur:** Klare Trennung von UI, State und Logik, State-Hoisting, keine
  UI-Logik im ViewModel.

---

## Screenshots

<p>
  <img src="./img/screen1.png" width="300" alt="Suchbildschirm LightMode">
  <img src="./img/screen2.png" width="300" alt="Suchbildschirm DarkMode">
  <img src="./img/screen4.png" width="300" alt="Detailansicht mit Screenshots">
  <img src="./img/screen3.png" width="300" alt="Favoriten-Liste">
</p>

---

## Features

- 🔍 **Spielsuche** nach Titel, Plattform, Genre, Bewertung
- 🏷️ **Filter & Sortierung** (Plattform, Genre, Bewertung, Erscheinungsjahr)
- ⭐ **Favoriten speichern** (Offline mit Room)
- 📝 **Detailseite** mit Beschreibung, Galerie, Entwickler, Plattformen, Metacritic, Spielzeit
- 🆕 **Listenansicht** für Neuerscheinungen & Top-rated
- 📤 **Spiele teilen** via Link
- ⚡ **Offline-Cache** für schnelle Anzeige & Fehlervermeidung
- 🎨 **Jetpack Compose UI** – modern, performant, flexibel
- 🏗️ **MVVM-Architektur** mit sauberem Repository-Pattern
- ⏳ **Ladeindikator & Error-UI** mit Retry-Funktion
- 🛡️ **Fehlerhinweise direkt im Feld** (z. B. „Keine Screenshots verfügbar“)
- 📝 **Logging** für Navigation und Datenübergabe

---

## Technischer Aufbau

### Projektstruktur (MVVM + Compose)

```bash
de.syntax-institut.androidabschlussprojekt
├── data/local         # Room: Entities, DAOs, DB
├── data/remote        # Retrofit DTOs + API-Service
├── data/repositories  # Kommunikation zwischen Datenquellen
├── di                 # Dependency Injection (Koin)
├── navigation         # Jetpack Navigation Komponenten
├── ui
│   ├── components     # Wiederverwendbare Composables
│   ├── screens        # Hauptscreens (Search, Detail, Favorites, Settings)
│   ├── theme          # Farben, Typographie, Shapes
│   └── viewmodels     # ViewModel-Logik & UI-State
├── utils              # Hilfsklassen (z. B. Resource.kt)
└── services           # z. B. Background-Tasks
```

### Datenhaltung & API

- **Favoriten & Detail-Cache:** Offline verfügbar mit Room
- **RAWG Video Games API:**
  - Base URL: `https://api.rawg.io/api/`
  - Endpoints: `/games?search=...`, `/games/{id}`
  - Filter: Plattform, Genre, Bewertung, Datum
  - API-Key in `local.properties` eintragen

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

- **Fehler pro Feld:**
  - Website oder Screenshots fehlen? → ErrorCard nur im jeweiligen Bereich
  - Echte API-/Netzwerkfehler? → Globale ErrorCard mit Retry
- **Null-Safety:**
  - Alle Datenzugriffe sind null-sicher, keine Crashes durch fehlende Felder
- **Logging:**
  - Navigation und Datenübergaben werden geloggt (z. B. gameId bei Detailaufruf)
- **Keine komplexen Objekte in Navigation:**
  - Es werden nur primitive Typen (Int, String) übergeben

---

## Setup

1. **API Key** erstellen: [RAWG API Docs](https://rawg.io/apidocs)
2. Key in `local.properties` eintragen: `RAWG_API_KEY=dein_key`
3. Projekt in Android Studio öffnen und ausführen

---

## Ausblick

- [ ] Push-Notifikationen zu neuen Top-Spielen
- [x] Dark Mode
- [x] Vollbild-Screenshot-Galerie in Detailseite
- [x] Fehlerbehandlung pro Feld (statt global)
- [x] Logging für Navigation und Fehler
- [x] Erweiterte Paging-Unterstützung
- [ ] Firebase-Integration für Sync & Analytics

---

## Lizenz

MIT License – siehe [LICENSE](LICENSE)

