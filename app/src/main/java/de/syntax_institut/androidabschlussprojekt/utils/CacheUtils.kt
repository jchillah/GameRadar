package de.syntax_institut.androidabschlussprojekt.utils

import de.syntax_institut.androidabschlussprojekt.data.local.dao.*
import de.syntax_institut.androidabschlussprojekt.data.local.models.*

/**
 * Utility für Cache-Statistiken und Optimierung.
 */
object CacheUtils {
    /**
     * Optimiert den Spiele-Cache, indem alte Einträge entfernt werden.
     * Entfernt Einträge, die älter als 7 Tage sind.
     */
    suspend fun optimizeCache(gameCacheDao: GameCacheDao) {
        try {
            val currentSize = gameCacheDao.getCacheSize()
            val oldestCacheTime = gameCacheDao.getOldestCacheTime()
            if (oldestCacheTime != null) {
                val sevenDaysAgo = System.currentTimeMillis() - (7 * 24 * 60 * 60 * 1000L)
                gameCacheDao.deleteExpiredGames(sevenDaysAgo)
                val newSize = gameCacheDao.getCacheSize()
                AppLogger.d("CacheUtils", "Cache optimiert: $currentSize -> $newSize Einträge")
            }
        } catch (e: Exception) {
            AppLogger.e("CacheUtils", "Fehler bei Cache-Optimierung", e)
        }
    }

    /**
     * Liefert Statistiken zum Spiele-Cache.
     */
    suspend fun getCacheStats(gameCacheDao: GameCacheDao): CacheStats {
        val totalSize = gameCacheDao.getCacheSize()
        // val oldestTime = gameCacheDao.getOldestCacheTime() // entfällt
        // val isExpired = oldestTime?.let { !NetworkUtils.isCacheValid(it) } != false // entfällt
        return CacheStats(
            count = totalSize,
            size = 0L // Platzhalter: Hier könnte die tatsächliche Cache-Größe in Bytes stehen, falls verfügbar
        )
    }

    /**
     * Berechnet eine empfohlene maximale Cachegröße (Anzahl Spiele) basierend auf dem freien Gerätespeicher.
     * Es werden 10% des freien Speichers für den Cache reserviert, durchschnittlich 0,5 MB pro Spiel.
     */
    fun calculateRecommendedMaxCacheSize(): Int {
        return try {
            val stat = android.os.StatFs(android.os.Environment.getDataDirectory().path)
            val bytesAvailable = stat.availableBytes
            val mbAvailable = bytesAvailable / (1024 * 1024)
            val cacheMb = (mbAvailable * 0.01).toInt() // 1% des freien Speichers
            val avgGameSizeMb = 0.5 // Durchschnittliche Größe pro Spiel in MB
            val maxGames = (cacheMb / avgGameSizeMb).toInt().coerceAtLeast(100000)
            maxGames
        } catch (_: Exception) {
            Int.MAX_VALUE
        }
    }
} 