package de.syntax_institut.androidabschlussprojekt.utils

import android.util.*

object AppLogger {
    fun debug(tag: String, message: String) {
        Log.d(tag, message)
    }

    fun info(tag: String, message: String) {
        Log.i(tag, message)
    }

    fun warn(tag: String, message: String) {
        Log.w(tag, message)
    }

    fun error(tag: String, message: String, throwable: Throwable? = null) {
        Log.e(tag, message, throwable)
    }
} 