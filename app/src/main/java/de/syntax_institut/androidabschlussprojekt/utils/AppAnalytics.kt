package de.syntax_institut.androidabschlussprojekt.utils

import android.content.*
import android.os.*
import com.google.firebase.analytics.*

/**
 * Utility-Objekt für Analytics-Events (Firebase, AdMob, App-Events).
 * Sendet nur Events, wenn der Nutzer dem Opt-In zugestimmt hat.
 *
 * - Modular, Clean Code, KDoc
 * - Unterstützt Custom Events, ScreenViews, Ad-Events, Performance, Fehler
 * - Analytics-Opt-In wird übergeben oder aus SettingsRepository gelesen
 */
object AppAnalytics {
    private var analytics: FirebaseAnalytics? = null
    private var analyticsEnabled: Boolean = false

    /** Initialisiert das Analytics-Objekt. Im Application-Objekt aufrufen. */
    fun init(context: Context, enabled: Boolean = false) {
        analytics = FirebaseAnalytics.getInstance(context)
        analyticsEnabled = enabled
    }

    /** Setzt den Opt-In-Status für Analytics. */
    fun setAnalyticsEnabled(enabled: Boolean) {
        analyticsEnabled = enabled
    }

    /** Trackt einen ScreenView. */
    fun trackScreenView(screenName: String) {
        if (!analyticsEnabled) return
        analytics?.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, Bundle().apply {
            putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
        })
    }

    /** Trackt ein generisches Event mit Parametern. */
    fun trackEvent(event: String, params: Map<String, Any?> = emptyMap()) {
        if (!analyticsEnabled) return
        val bundle = Bundle()
        params.forEach { (k, v) ->
            when (v) {
                is String -> bundle.putString(k, v)
                is Int -> bundle.putInt(k, v)
                is Boolean -> bundle.putBoolean(k, v)
                is Float -> bundle.putFloat(k, v)
                is Double -> bundle.putDouble(k, v)
                is Long -> bundle.putLong(k, v)
            }
        }
        analytics?.logEvent(event, bundle)
    }

    /** Trackt ein AdMob-Event (z.B. Impression, Klick, Fehler). */
    fun trackAdEvent(context: Context, adType: String, action: String) {
        if (!analyticsEnabled) return
        val bundle = Bundle().apply {
            putString("ad_type", adType)
            putString("action", action)
        }
        analytics?.logEvent("ad_event", bundle)
    }

    /** Trackt eine Cache-Operation (z.B. Export/Import). */
    fun trackCacheOperation(operation: String, itemCount: Int, success: Boolean) {
        if (!analyticsEnabled) return
        val bundle = Bundle().apply {
            putString("operation", operation)
            putInt("item_count", itemCount)
            putBoolean("success", success)
        }
        analytics?.logEvent("cache_operation", bundle)
    }

    /** Trackt eine Nutzeraktion (z.B. Button-Klick, Favorit, Wishlist). */
    fun trackUserAction(action: String, itemId: Int? = null) {
        if (!analyticsEnabled) return
        val bundle = Bundle().apply {
            putString("action", action)
            itemId?.let { putInt("item_id", it) }
        }
        analytics?.logEvent("user_action", bundle)
    }

    /** Trackt eine Game-Interaktion (z.B. Favorit, Wishlist). */
    fun trackGameInteraction(gameId: String, action: String) {
        if (!analyticsEnabled) return
        val bundle = Bundle().apply {
            putString("game_id", gameId)
            putString("action", action)
        }
        analytics?.logEvent("game_interaction", bundle)
    }

    /** Trackt eine Performance-Metrik (z.B. Ladezeit, Speicher). */
    fun trackPerformanceMetric(metric: String, value: Number, unit: String? = null) {
        if (!analyticsEnabled) return
        val bundle = Bundle().apply {
            putString("metric", metric)
            putDouble("value", value.toDouble())
            unit?.let { putString("unit", it) }
        }
        analytics?.logEvent("performance_metric", bundle)
    }

    /** Trackt einen Fehler. */
    fun trackError(error: String, context: String? = null) {
        if (!analyticsEnabled) return
        val bundle = Bundle().apply {
            putString("error", error)
            context?.let { putString("context", it) }
        }
        analytics?.logEvent("app_error", bundle)

        // Crashlytics Error Recording
        CrashlyticsHelper.recordUiError(
            context ?: "AppAnalytics",
            "trackError",
            error
        )
    }

    /** Trackt das Aktivieren/Deaktivieren von Crashlytics. */
    fun trackCrashlyticsEnabled(enabled: Boolean) {
        if (!analyticsEnabled) return
        val bundle = Bundle().apply {
            putBoolean("crashlytics_enabled", enabled)
        }
        analytics?.logEvent("crashlytics_opt_in", bundle)
    }
}
