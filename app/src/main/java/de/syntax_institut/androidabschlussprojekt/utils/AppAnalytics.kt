package de.syntax_institut.androidabschlussprojekt.utils

import android.content.*
import com.google.firebase.analytics.*

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
                val bundle = android.os.Bundle().apply {
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
                val bundle = android.os.Bundle().apply {
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
                val bundle = android.os.Bundle().apply {
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
        val params = if (gameId != null) {
            mapOf("game_id" to gameId)
        } else {
            emptyMap()
        }
        trackEvent("user_action_$action", params)
    }
}
