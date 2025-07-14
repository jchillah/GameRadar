package de.syntax_institut.androidabschlussprojekt.data

/**
 * Zentrale Konstanten für Datenbanknamen, Tabellennamen, API-Parameter, Paging-Keys usw. Alle Magic
 * Strings werden hier ausgelagert und im Code referenziert.
 */
object Constants {

    // Allgemeine Fehler-Konstante für Logging und Fehlertexte
    const val ERROR = "Fehler"
    const val ERROR_UNKNOWN = "Unbekannter Fehler"

    // Datenbanknamen
    const val DATABASE_NAME = "game_database"
    const val GAME_CACHE_TABLE = "game_cache"
    const val GAME_DETAIL_CACHE_TABLE = "game_detail_cache"

    // Table-Name für Favoriten-Entity (für Room)
    const val FAVORITE_GAME_TABLE = "favorite_games"

    // Table-Name für Wishlist-Entity (für Room)
    const val WISHLIST_GAME_TABLE = "wishlist_games"

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

    // Standard-Delay für Cache-Monitoring (ms)
    const val CACHE_MONITORING_DELAY = 500L

    // Intervall für Cache-Überwachung (ms)
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
    const val PREF_LAST_KNOWN_GAME_IDS = "last_known_game_ids"
    const val PREF_LAST_KNOWN_GAME_SLUGS = "last_known_game_slugs"

    // Notification Channel
    const val NOTIFICATION_CHANNEL_ID = "new_games"

    // API Endpoints
    const val ENDPOINT_GAMES = "games"
    const val ENDPOINT_ID = "id"
    const val ENDPOINT_GAME_DETAIL = "games/{id}"
    const val ENDPOINT_GAME_SCREENSHOTS = "games/{id}/screenshots"
    const val ENDPOINT_PLATFORMS = "platforms"
    const val ENDPOINT_GENRES = "genres"
    const val ENDPOINT_GAME_MOVIES = "games/{id}/movies"

    const val LAST_SYNC_TIME = "last_sync_time"

    const val NEW_GAME_NOTIFICATION_ID = 1001
}
