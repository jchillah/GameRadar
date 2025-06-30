# GameRadar 🎮  
**„Entdecke deine nächste Gaming-Leidenschaft"**

GameRadar hilft dir, neue Spiele zu entdecken, nach Plattformen, Genre und Bewertung zu filtern, Favoriten zu speichern und direkt zu teilen. Ideal für Gamer:innen, die schnell passende Spiele finden und im Blick behalten möchten – übersichtlich, modern und mobil optimiert.

Für wen ist die App? Spieler:innen, die auf der Suche nach ihrem nächsten Spiel-Hit sind – und zwar unkompliziert, ohne Werbung oder überladene Interfaces. GameRadar fokussiert sich aufs Wesentliche: Entdecken, Favorisieren, Teilen.

GameRadar ist anders:  
Keine überfrachteten Inhalte, keine gesponserten Spiele – nur echte Nutzer-Empfehlungen, blitzschnell gefiltert nach deinen Bedürfnissen.

## Design
Ich Füge am Ende echte Screenshots ein:

<p>
  <img src="./img/screen1.png" width="300" alt="GameRadar Suchbildschirm mit Spieleliste und Suchleiste">
  <img src="./img/screen2.png" width="300" alt="GameRadar Detailansicht eines Spiels mit Screenshots und Informationen">
  <img src="./img/screen3.png" width="300" alt="GameRadar Favoriten-Liste mit gespeicherten Spielen">
</p>

## Features

- [ ] Spielsuche nach Titel  
- [ ] Filter & Sortierung (Plattform, Genre, Bewertung, Erscheinungsjahr)  
- [ ] Favoriten speichern (Offline mit Room)  
- [ ] Detailseite mit Beschreibung, Bildergalerie, Entwickler, Plattformen usw.  
- [ ] Listenansicht für Neuerscheinungen & Top-rated  
- [ ] Spiele teilen via Link/Bild  
- [ ] Offline-Cache für schnelles Anzeigen & Fehlervermeidung  
- [ ] Moderne UI mit Jetpack Compose  
- [ ] MVVM-Architektur mit sauberem Repository-Pattern  
- [ ] Ladeindikator & Error-UI mit Retry-Funktion  

## Technischer Aufbau

### Projektaufbau  
Gefolgt ist deine Ordnerstruktur in **Kotlin + MVVM**:

```bash
de.syntax-institut.androidabschlussprojekt
├── data/local # Room: Entities, DAOs, DB
├── data/remote # Retrofit DTOs + API-Service
├── data/repositories # Kommunikation zwischen Datenquellen
├── di # Dependency Injection (Hilt/Koin)
├── navigation # Jetpack Navigation Komponenten
├── ui
│ ├── components # Wiederverwendbare Composables
│ ├── screens # game_list, game_detail, favorites
│ ├── theme # Farben, Typographie, Shapes
│ └── viewmodels # ViewModel-Logik & UI-State
├── utils # Hilfsklassen (z. B. Resource.kt)
└── services # z. B. Background-Tasks oder WorkManager
```
---

### Datenspeicherung  
- **Favoriten & Detail-Cache:** Offline verfügbar mit **Room**, um schnelle Zugriffe garantieren zu können  
- Fokus auf **Offline-First**: Überprüfte Daten auch ohne Internet verfügbar

### API Calls  
- **RAWG Video Games API**  
  - Base URL: `https://api.rawg.io/api/`  
  - Jeder Request enthält den API-Key über `?key=DEIN_API_KEY` :contentReference[oaicite:1]{index=1}  
  - Endpoints:  
    - Spiele suchen (`/games?search=...`)  
    - Spiel-Details (`/games/{id}`)  
    - Filtermöglichkeiten über Plattform, Genre, Bewertung, Datum

### 3rd-Party Frameworks  
- **Retrofit** + **Moshi** für API-Kommunikation & JSON-Mapping  
- **Room** für lokale Datenhaltung  
- **Jetpack Compose** (UI) + Navigation  
- **Coil** für Bildladen  
- **Accompanist** für Paging & SwipeRefresh  
- **Hilt/Koin** für Dependency Injection  
- Optional später: **Firebase Crashlytics & Analytics**

## Ausblick

- [ ] Push-Notifikationen zu neuen Top-Spielen  
- [ ] Dark Mode  
- [ ] Vollbild-Screenshot-Galerie in Detailseite  
- [ ] Erweiterte Paging-Unterstützung  
- [ ] Firebase-Integration für Sync & Analytics

---

## Setup  

1. **API Key** erstellen: [RAWG API Docs](https://rawg.io/apidocs) :contentReference[oaicite:2]{index=2}  
2. Key in `local.properties` eintragen

---

