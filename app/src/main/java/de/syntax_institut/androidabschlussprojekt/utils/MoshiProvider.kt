package de.syntax_institut.androidabschlussprojekt.utils

import com.squareup.moshi.*
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

object MoshiProvider {
    val moshi: Moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
}
