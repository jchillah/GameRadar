package de.syntax_institut.androidabschlussprojekt.utils

import de.syntax_institut.androidabschlussprojekt.data.*

object ErrorHandler {
    fun handleException(
        e: Exception,
        fallbackMessage: String = Constants.ERROR_UNKNOWN_DEFAULT,
    ): String {
        AppLogger.e("ErrorHandler", fallbackMessage, e)
        return e.localizedMessage ?: fallbackMessage
    }
} 