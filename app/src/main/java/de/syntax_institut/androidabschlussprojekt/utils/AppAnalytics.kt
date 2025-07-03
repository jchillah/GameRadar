package de.syntax_institut.androidabschlussprojekt.utils

import android.util.*

object Analytics {
    fun trackEvent(eventName: String, parameters: Map<String, Any> = emptyMap()) {
        Log.d("Analytics", "Event: $eventName, Parameters: $parameters")
        // Hier würde die echte Analytics-Implementation stehen
    }

    fun trackScreenView(screenName: String) {
        Log.d("Analytics", "Screen View: $screenName")
    }

    fun trackError(error: String, context: String) {
        Log.e("Analytics", "Error in $context: $error")
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
