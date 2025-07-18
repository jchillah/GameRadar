package de.syntax_institut.androidabschlussprojekt.data.repositories

import android.content.*
import androidx.core.content.*
import de.syntax_institut.androidabschlussprojekt.data.*
import de.syntax_institut.androidabschlussprojekt.data.local.models.*
import de.syntax_institut.androidabschlussprojekt.data.remote.*
import de.syntax_institut.androidabschlussprojekt.data.remote.mapper.*

suspend fun checkForNewGamesAndUpdatePrefs(
    api: RawgApi,
    prefs: SharedPreferences,
    count: Int = 10,
): List<Game> {
    // 1. Hole die letzten Spiele von der API
    val response = api.searchGames(page = 1, pageSize = count, query = "")
    if (!response.isSuccessful) return emptyList()
    val games = response.body()?.results?.map { it.toDomain() } ?: return emptyList()

    // 2. Lade die letzten bekannten IDs/Slugs aus den SharedPreferences
    val lastIds = prefs.getStringSet(Constants.PREF_LAST_KNOWN_GAME_IDS, emptySet()) ?: emptySet()
    val lastSlugs =
        prefs.getStringSet(Constants.PREF_LAST_KNOWN_GAME_SLUGS, emptySet()) ?: emptySet()

    // 3. Finde neue Spiele (ID oder Slug noch nicht bekannt)
    val newGames = games.filter { it.id.toString() !in lastIds || it.slug !in lastSlugs }

    // 4. Aktualisiere die gespeicherten IDs/Slugs (nur die neuesten)
    prefs.edit {
        putStringSet(Constants.PREF_LAST_KNOWN_GAME_IDS, games.map { it.id.toString() }.toSet())
        putStringSet(Constants.PREF_LAST_KNOWN_GAME_SLUGS, games.map { it.slug }.toSet())
    }

    return newGames
} 