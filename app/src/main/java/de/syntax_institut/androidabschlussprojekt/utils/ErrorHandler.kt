package de.syntax_institut.androidabschlussprojekt.utils

object ErrorHandler {
    fun handleException(
        e: Exception,
        fallbackMessage: String = "Unbekannter Fehler",
    ): String {
        AppLogger.e("ErrorHandler", fallbackMessage, e)
        return e.localizedMessage ?: fallbackMessage
    }
} 