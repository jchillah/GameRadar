package de.syntax_institut.androidabschlussprojekt.utils

import com.squareup.moshi.*
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

/**
 * Provider für die Moshi-Instanz mit Kotlin-Adapter.
 *
 * Stellt eine zentrale Moshi-Instanz für JSON-Serialisierung/Deserialisierung bereit.
 * Verwendet KotlinJsonAdapterFactory für bessere Kotlin-Unterstützung.
 */
object MoshiProvider {
    /**
     * Moshi-Instanz mit Kotlin-Adapter für JSON-Parsing.
     */
    val moshi: Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()
}
