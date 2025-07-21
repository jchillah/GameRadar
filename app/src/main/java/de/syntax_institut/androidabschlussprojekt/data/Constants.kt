package de.syntax_institut.androidabschlussprojekt.data

/**
 * Zentrale Konstanten für Datenbanknamen, Tabellennamen, API-Parameter, Paging-Keys usw. Alle Magic
 * Strings werden hier ausgelagert und im Code referenziert.
 *
 * UI-Strings (z.B. Fehlermeldungen für Nutzer:innen) gehören in strings.xml und NICHT hierher!
 */
object Constants {

    /** Allgemeine Fehler-Konstante für Logging und Fehlertexte (nur intern, nicht für UI) */
    const val ERROR = "Fehler"

    /** Allgemeiner unbekannter Fehler (nur intern, nicht für UI) */
    const val ERROR_UNKNOWN = "Unbekannter Fehler"

    /** Name der Room-Datenbank */
    const val DATABASE_NAME = "game_database"

    /** Tabelle für gecachte Spiele */
    const val GAME_CACHE_TABLE = "game_cache"

    /** Tabelle für gecachte Spieldetails */
    const val GAME_DETAIL_CACHE_TABLE = "game_detail_cache"

    /** Tabelle für Favoriten-Spiele */
    const val FAVORITE_GAME_TABLE = "favorite_games"

    /** Tabelle für Wunschlisten-Spiele */
    const val WISHLIST_GAME_TABLE = "wishlist_games"

    // API-Parameter
    const val API_KEY_PARAM = "key"
    const val PAGE_PARAM = "page"
    const val PAGE_SIZE_PARAM = "page_size"
    const val SEARCH_PARAM = "search"
    const val GENRES_PARAM = "genres"
    const val PLATFORMS_PARAM = "platforms"
    const val ORDERING_PARAM = "ordering"

    /** Name des Workers für neue Spiele */
    const val NEW_GAME_WORKER_NAME = "NewGameWorker"

    /** Default-JSON-Array (z.B. für leere Listen in DAOs/Mappings) */
    const val EMPTY_JSON_ARRAY = "[]"

    /** Standard-Delay für Cache-Monitoring (ms) */
    const val CACHE_MONITORING_DELAY = 500L

    /** Intervall für Cache-Überwachung (ms) */
    const val CACHE_MONITORING_INTERVAL = 60000L

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
    const val PREF_ADS_ENABLED = "ads_enabled"
    const val PREF_ANALYTICS_ENABLED = "analytics_enabled"

    // Notification-Konstanten
    const val NOTIFICATION_CHANNEL_ID = "new_games_channel"
    const val NEW_GAME_NOTIFICATION_ID = 1001

    // Cache-Konstanten
    const val CACHE_SIZE_LIMIT = 100 * 1024 * 1024 // 100 MB
    const val CACHE_CLEANUP_THRESHOLD = 0.8f // 80% Nutzung
    const val CACHE_RETENTION_DAYS = 7L

    // API-Konstanten
    const val API_TIMEOUT_SECONDS = 30L
    const val API_RETRY_COUNT = 3
    const val API_BASE_URL = "https://api.rawg.io/api/"
    const val BASE_URL = "https://api.rawg.io/api/"
    const val API_PAGE_SIZE = 20

    // API-Endpoints
    const val ENDPOINT_GAMES = "games"
    const val ENDPOINT_GAME_DETAIL = "games/{id}"
    const val ENDPOINT_GAME_SCREENSHOTS = "games/{id}/screenshots"
    const val ENDPOINT_GAME_MOVIES = "games/{id}/movies"
    const val ENDPOINT_PLATFORMS = "platforms"
    const val ENDPOINT_GENRES = "genres"
    const val ENDPOINT_ID = "id"

    // SharedPreferences Keys für Game-Prefs
    const val PREF_LAST_KNOWN_GAME_IDS = "last_known_game_ids"
    const val PREF_LAST_KNOWN_GAME_SLUGS = "last_known_game_slugs"
    const val LAST_SYNC_TIME = "last_sync_time"

    // Performance-Konstanten
    const val PERFORMANCE_THRESHOLD_MS = 1000L // 1 Sekunde
    const val MEMORY_WARNING_THRESHOLD_MB = 100L // 100 MB

    // Navigation-Konstanten
    const val NAVIGATION_TIMEOUT_MS = 5000L // 5 Sekunden

    // Export/Import-Konstanten
    const val EXPORT_FILENAME_PREFIX = "gameradar_export"
    const val EXPORT_FILE_EXTENSION = ".json"
    const val MAX_EXPORT_SIZE_BYTES = 10 * 1024 * 1024 // 10 MB

    // Analytics-Konstanten
    const val ANALYTICS_BATCH_SIZE = 10
    const val ANALYTICS_FLUSH_INTERVAL_MS = 60000L // 1 Minute

    // Error-Codes
    const val ERROR_CODE_NETWORK = 1001
    const val ERROR_CODE_API = 1002
    const val ERROR_CODE_DATABASE = 1003
    const val ERROR_CODE_CACHE = 1004
    const val ERROR_CODE_IMPORT = 1005
    const val ERROR_CODE_EXPORT = 1006

    // Feature-Flags
    const val FEATURE_REWARDED_ADS = true
    const val FEATURE_ANALYTICS = true
    const val FEATURE_CRASHLYTICS = true
    const val FEATURE_CACHE_OPTIMIZATION = true
    const val FEATURE_AUTO_SYNC = true

    // Debug-Konstanten
    const val DEBUG_LOG_ENABLED = true
    const val DEBUG_PERFORMANCE_MONITORING = true
    const val DEBUG_CRASHLYTICS_LOGGING = true
}
