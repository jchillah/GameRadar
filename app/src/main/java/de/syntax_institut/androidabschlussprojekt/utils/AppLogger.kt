package de.syntax_institut.androidabschlussprojekt.utils

import android.util.*
import de.syntax_institut.androidabschlussprojekt.*

object AppLogger {
    private const val GLOBAL_TAG = "GameRadar"

    enum class Level { DEBUG, INFO, WARN, ERROR }

    var isLoggingEnabled: Boolean = BuildConfig.DEBUG

    fun d(tag: String, message: String) = log(Level.DEBUG, tag, message)
    fun i(tag: String, message: String) = log(Level.INFO, tag, message)
    fun w(tag: String, message: String) = log(Level.WARN, tag, message)
    fun e(tag: String, message: String, throwable: Throwable? = null) =
        log(Level.ERROR, tag, message, throwable)

    private fun log(level: Level, tag: String, message: String, throwable: Throwable? = null) {
        if (!isLoggingEnabled) return
        val fullTag = "$GLOBAL_TAG-$tag"
        when (level) {
            Level.DEBUG -> Log.d(fullTag, message)
            Level.INFO -> Log.i(fullTag, message)
            Level.WARN -> Log.w(fullTag, message)
            Level.ERROR -> Log.e(fullTag, message, throwable)
        }
    }
} 