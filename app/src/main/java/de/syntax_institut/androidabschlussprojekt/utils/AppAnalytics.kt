package de.syntax_institut.androidabschlussprojekt.utils

import android.content.*
import com.google.firebase.analytics.*
import de.syntax_institut.androidabschlussprojekt.*

object AppAnalytics {
    // Singleton für FirebaseAnalytics
    @Volatile
    private var firebaseAnalytics: FirebaseAnalytics? = null

    fun init(context: Context) {
        if (firebaseAnalytics == null) {
            synchronized(this) {
                if (firebaseAnalytics == null) {
                    try {
                        firebaseAnalytics = FirebaseAnalytics.getInstance(context)
                        AppLogger.d("Analytics", "FirebaseAnalytics initialisiert")

                        // Analytics-Debug-Modus für Development aktivieren
                        if (BuildConfig.DEBUG) {
                            firebaseAnalytics?.setAnalyticsCollectionEnabled(true)
                        }
                    } catch (e: Exception) {
                        AppLogger.e(
                            "Analytics",
                            "FirebaseAnalytics konnte nicht initialisiert werden: ${e.localizedMessage}"
                        )
                    }
                }
            }
        }
    }

    fun trackEvent(eventName: String, parameters: Map<String, Any> = emptyMap()) {
        AppLogger.d("Analytics", "Event: $eventName, Parameters: $parameters")
        firebaseAnalytics?.let { fa ->
            try {
                val bundle =
                    android.os.Bundle().apply {
                        parameters.forEach { (key, value) ->
                            when (value) {
                                is String -> putString(key, value)
                                is Int -> putInt(key, value)
                                is Long -> putLong(key, value)
                                is Double -> putDouble(key, value)
                                is Float -> putFloat(key, value)
                                is Boolean -> putBoolean(key, value)
                                else -> putString(key, value.toString())
                            }
                        }
                    }
                fa.logEvent(eventName, bundle)
            } catch (e: Exception) {
                AppLogger.e(
                    "Analytics",
                    "Fehler beim Senden des Events an Firebase: ${e.localizedMessage}"
                )
            }
        }
    }

    fun trackScreenView(screenName: String) {
        AppLogger.d("Analytics", "Screen View: $screenName")
        firebaseAnalytics?.let { fa ->
            try {
                val bundle =
                    android.os.Bundle().apply {
                        putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
                        putString(FirebaseAnalytics.Param.SCREEN_CLASS, "ComposeScreen")
                    }
                fa.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle)
            } catch (e: Exception) {
                AppLogger.e(
                    "Analytics",
                    "Fehler beim Senden des ScreenViews an Firebase: ${e.localizedMessage}"
                )
            }
        }
    }

    fun trackError(error: String, context: String) {
        AppLogger.e("Analytics", "Error in $context: $error")
        firebaseAnalytics?.let { fa ->
            try {
                val bundle =
                    android.os.Bundle().apply {
                        putString("error_message", error)
                        putString("error_context", context)
                    }
                fa.logEvent("app_error", bundle)
            } catch (e: Exception) {
                AppLogger.e(
                    "Analytics",
                    "Fehler beim Senden des Fehlers an Firebase: ${e.localizedMessage}"
                )
            }
        }
    }

    fun trackUserAction(action: String, gameId: Int? = null) {
        val params =
            if (gameId != null) {
                mapOf("game_id" to gameId)
            } else {
                emptyMap()
            }
        trackEvent("user_action_$action", params)
    }

    // Neue erweiterte Tracking-Funktionen
    fun trackGameInteraction(action: String, gameId: Int, gameTitle: String? = null) {
        val params = mutableMapOf<String, Any>("game_id" to gameId, "action_type" to action)
        gameTitle?.let { params["game_title"] = it }
        trackEvent("game_interaction", params)
    }

    fun trackSearchQuery(query: String, resultCount: Int) {
        trackEvent(
            "search_performed",
            mapOf("search_query" to query, "result_count" to resultCount)
        )
    }

    fun trackFilterUsage(platforms: List<String>, genres: List<String>, rating: Float) {
        trackEvent(
            "filter_applied",
            mapOf(
                "platforms_count" to platforms.size,
                "genres_count" to genres.size,
                "min_rating" to rating
            )
        )
    }

    fun trackPerformanceMetric(metricName: String, value: Long, unit: String = "ms") {
        trackEvent(
            "performance_metric",
            mapOf("metric_name" to metricName, "value" to value, "unit" to unit)
        )
    }

    fun trackCacheOperation(operation: String, cacheSize: Int, success: Boolean) {
        trackEvent(
            "cache_operation",
            mapOf("operation" to operation, "cache_size" to cacheSize, "success" to success)
        )
    }

    fun trackAppFeatureUsage(featureName: String, enabled: Boolean) {
        trackEvent("feature_usage", mapOf("feature_name" to featureName, "enabled" to enabled))
    }

    fun trackNetworkStatus(isOnline: Boolean, requestType: String) {
        trackEvent("network_status", mapOf("is_online" to isOnline, "request_type" to requestType))
    }

    fun trackImageQualitySetting(quality: String) {
        trackEvent("image_quality_changed", mapOf("quality_setting" to quality))
    }

    fun trackLanguageChange(language: String) {
        trackEvent("language_changed", mapOf("selected_language" to language))
    }

    fun trackDarkModeToggle(enabled: Boolean) {
        trackEvent("dark_mode_toggled", mapOf("dark_mode_enabled" to enabled))
    }

    fun trackNotificationPermission(granted: Boolean) {
        trackEvent("notification_permission", mapOf("permission_granted" to granted))
    }

    fun trackAppSession(duration: Long) {
        trackEvent("app_session", mapOf("session_duration_seconds" to duration))
    }

    fun trackCrashlyticsEnabled(enabled: Boolean) {
        trackEvent("crashlytics_status", mapOf("crashlytics_enabled" to enabled))
    }
}
