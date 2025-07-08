package de.syntax_institut.androidabschlussprojekt.data

/**
 * Zentrale Konstanten für Datenbanknamen, Tabellennamen, API-Parameter, Paging-Keys usw.
 * Alle Magic Strings werden hier ausgelagert und im Code referenziert.
 */
object Constants {

    // Datenbanknamen
    const val DATABASE_NAME = "game_database"
    const val GAME_CACHE_TABLE = "game_cache"
    const val GAME_DETAIL_CACHE_TABLE = "game_detail_cache"

    // Table-Name für Favoriten-Entity (für Room)
    const val FAVORITE_GAME_TABLE = "favorite_games"

    // API-Parameter
    const val API_KEY_PARAM = "key"
    const val PAGE_PARAM = "page"
    const val PAGE_SIZE_PARAM = "page_size"
    const val SEARCH_PARAM = "search"
    const val GENRES_PARAM = "genres"
    const val PLATFORMS_PARAM = "platforms"
    const val ORDERING_PARAM = "ordering"

    // Worker
    const val NEW_GAME_WORKER_NAME = "NewGameWorker"

    // Default-JSON-Array (z.B. für leere Listen in DAOs/Mappings)
    const val EMPTY_JSON_ARRAY = "[]"

    // Standard-Fehlertext (z.B. für ErrorHandler)
    const val ERROR_UNKNOWN_DEFAULT = "Ein unbekannter Fehler ist aufgetreten"

    // Standard-Delay für Cache-Monitoring (ms)
    const val CACHE_MONITORING_DELAY = 500L

    // Intervall für Cache-Überwachung (ms)
    const val CACHE_MONITORING_INTERVAL = 60000L

    // Fehlertexte Favoriten
    const val ERROR_ADD_FAVORITE = "Fehler beim Hinzufügen des Favoriten"
    const val ERROR_REMOVE_FAVORITE = "Fehler beim Entfernen des Favoriten"
    const val ERROR_TOGGLE_FAVORITE = "Fehler beim Umschalten des Favoriten"
    const val ERROR_CLEAR_FAVORITES = "Fehler beim Leeren der Favoriten"

    // Fehlertexte GameRepository
    const val ERROR_NO_CONNECTION_AND_NO_CACHE = "Keine Verbindung und kein Cache verfügbar"
    const val ERROR_SERVER = "Serverfehler: "
    const val ERROR_NETWORK = "Netzwerkfehler: "
    const val ERROR_NO_PLATFORM_DATA = "Keine Plattformdaten verfügbar"
    const val ERROR_API = "API-Fehler: "
    const val ERROR_NO_GENRE_DATA = "Keine Genre-Daten verfügbar"

    // SharedPreferences Keys
    const val PREFS_NAME = "gameradar_settings"
    const val PREF_NOTIFICATIONS_ENABLED = "notifications_enabled"
    const val PREF_AUTO_REFRESH_ENABLED = "auto_refresh_enabled"
    const val PREF_IMAGE_QUALITY = "image_quality"
    const val PREF_LANGUAGE = "language"
    const val PREF_GAMING_MODE_ENABLED = "gaming_mode_enabled"
    const val PREF_PERFORMANCE_MODE_ENABLED = "performance_mode_enabled"
    const val PREF_SHARE_GAMES_ENABLED = "share_games_enabled"
    const val PREF_DARK_MODE_ENABLED = "dark_mode_enabled"
    const val PREF_LAST_KNOWN_GAME_IDS = "last_known_game_ids"
    const val PREF_LAST_KNOWN_GAME_SLUGS = "last_known_game_slugs"

    // Notification Channel
    const val NOTIFICATION_CHANNEL_ID = "new_games"
    const val NOTIFICATION_CHANNEL_NAME = "Neue Spiele"
    const val NOTIFICATION_CHANNEL_DESC = "Benachrichtigungen über neue Spiele"
    const val NOTIFICATION_TITLE_NEW_GAME = "Neues Spiel verfügbar!"
    const val NOTIFICATION_TITLE_TEST = "Testspiel: Notification"
    const val NOTIFICATION_TEXT_TEST = "Dies ist eine Test-Benachrichtigung"

    // API Endpunkte (nur als Referenz, falls benötigt)
    const val ENDPOINT_GAMES = "games"
    const val ENDPOINT_ID = "id"
    const val ENDPOINT_GAME_DETAIL = "games/{id}"
    const val ENDPOINT_GAME_SCREENSHOTS = "games/{id}/screenshots"
    const val ENDPOINT_PLATFORMS = "platforms"
    const val ENDPOINT_GENRES = "genres"
    const val ENDPOINT_GAME_MOVIES = "games/{id}/movies"

    // Fehlertexte & UI-Texte
    const val ERROR = "Fehler"
    const val ERROR_UNKNOWN = "Unbekannter Fehler"
    const val ERROR_LOAD = "Fehler beim Laden"
    const val ERROR_LOAD_PLATFORMS = "Fehler beim Laden der Plattformen: "
    const val ERROR_LOAD_GENRES = "Fehler beim Laden der Genres: "
    const val ERROR_CLEAR_CACHE = "Fehler beim Leeren des Caches"
    const val ERROR_NO_CONNECTION = "Keine Internetverbindung"
    const val ERROR_CHECK_CONNECTION = "Überprüfe deine Verbindung und versuche es erneut"
    const val DIALOG_DELETE_ALL_FAVORITES_TITLE = "Alle Favoriten löschen?"
    const val DIALOG_DELETE_ALL_CONFIRM = "Löschen"
    const val DIALOG_DELETE_ALL_CANCEL = "Abbrechen"
    const val DIALOG_RESET_DATABASE_TITLE = "Datenbank zurücksetzen"
    const val DIALOG_RESET_DATABASE_SUBTITLE = "Löscht alle Favoriten und Cache-Daten"
    const val DIALOG_RESET_DATABASE_TEXT =
        "Möchten Sie wirklich alle Favoriten und Cache-Daten löschen? Diese Aktion kann nicht rückgängig gemacht werden."
    const val UI_PUSH_NOTIFICATIONS = "Push-Benachrichtigungen"
    const val UI_NEW_GAMES_AND_UPDATES = "Neue Spiele und Updates erhalten"
    const val UI_GAMING_MODE = "Gaming-Modus"
    const val UI_GAMING_MODE_DESC = "Optimierte Darstellung für Gaming"
    const val UI_PERFORMANCE_MODE = "Performance-Modus"
    const val UI_PERFORMANCE_MODE_DESC = "Schnellere Ladezeiten"
    const val UI_SHARE_GAMES = "Spiele teilen"
    const val UI_SHARE_GAMES_DESC = "Spiele mit Freunden teilen"
    const val UI_DARK_MODE = "Dunkles Design"
    const val UI_DARK_MODE_DESC = "Aktiviere den Dark Mode"

    // Empty State & Bewertung
    const val EMPTY_STATE_PREFIX = "Keine "
    const val EMPTY_STATE_SUFFIX = " gefunden."
    const val EMPTY_STATE_OFFLINE_PREFIX = "Offline: Keine "
    const val EMPTY_STATE_OFFLINE_SUFFIX = " verfügbar."
    const val NO_RATING = "Keine Bewertung"
    const val EMPTY_STATE_NO_RESULTS = "Keine Ergebnisse"
    const val EMPTY_STATE_NO_RESULTS_MESSAGE =
        "Versuche andere Suchbegriffe oder Filter zu verwenden."
    const val EMPTY_STATE_NO_FAVORITES = "Keine Favoriten"
    const val EMPTY_STATE_NO_FAVORITES_MESSAGE =
        "Du hast noch keine Spiele zu deinen Favoriten hinzugefügt."
    const val EMPTY_STATE_DISCOVER_GAMES = "Spiele entdecken"
    const val ERROR_CARD_DEFAULT_TITLE = "Fehler aufgetreten"
}