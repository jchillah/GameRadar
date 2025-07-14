package de.syntax_institut.androidabschlussprojekt.utils

import android.util.*
import com.squareup.moshi.*
import retrofit2.*
import java.io.*

/**
 * Zentrale Fehlerbehandlung für Netzwerk- und JSON-Fehler (Retrofit/Moshi). Gibt eine
 * benutzerfreundliche Fehlermeldung zurück und loggt die Exception.
 */
object ErrorHandler {
    fun handle(e: Throwable, tag: String = "ErrorHandler"): String {
        return when (e) {
            is HttpException -> {
                val code = e.code()
                val msg = "HTTP-Fehler $code: ${e.message()}"
                Log.e(tag, msg, e)
                msg
            }

            is JsonDataException -> {
                val msg = "JSON-Datenfehler: ${e.localizedMessage}"
                Log.e(tag, msg, e)
                msg
            }

            is JsonEncodingException -> {
                val msg = "JSON-Encoding-Fehler: ${e.localizedMessage}"
                Log.e(tag, msg, e)
                msg
            }

            is ClassNotFoundException -> {
                val msg = "Adapter-Klasse nicht gefunden: ${e.localizedMessage}"
                Log.e(tag, msg, e)
                msg
            }

            is IOException -> {
                val msg = "Netzwerkfehler: ${e.localizedMessage}"
                Log.e(tag, msg, e)
                msg
            }

            else -> {
                val msg = "Unbekannter Fehler: ${e.localizedMessage}"
                Log.e(tag, msg, e)
                msg
            }
        }
    }
}
