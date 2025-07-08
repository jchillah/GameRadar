package de.syntax_institut.androidabschlussprojekt.utils

object Analytics {
    fun trackEvent(eventName: String, parameters: Map<String, Any> = emptyMap()) {
        AppLogger.d("Analytics", "Event: $eventName, Parameters: $parameters")
        // TODO: Implement analytics tracking mit Firebase Analytics
    }

    fun trackScreenView(screenName: String) {
        AppLogger.d("Analytics", "Screen View: $screenName")
    }

    fun trackError(error: String, context: String) {
        AppLogger.e("Analytics", "Error in $context: $error")
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
