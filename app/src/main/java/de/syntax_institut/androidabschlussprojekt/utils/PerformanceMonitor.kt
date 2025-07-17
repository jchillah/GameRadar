package de.syntax_institut.androidabschlussprojekt.utils

import android.os.*
import java.util.concurrent.*

/**
 * Performance Monitor für detaillierte Performance-Tracking mit Firebase Analytics. Folgt Clean
 * Code Best Practices: Single Responsibility, DRY, KISS.
 */
object PerformanceMonitor {
    private val timers = ConcurrentHashMap<String, Long>()
    private val memoryUsage = ConcurrentHashMap<String, Long>()
    private val performanceMetrics = ConcurrentHashMap<String, MutableList<Long>>()

    /** Startet einen Timer für Performance-Messung. */
    fun startTimer(timerName: String) {
        timers[timerName] = SystemClock.elapsedRealtime()
        AppLogger.d("Performance", "Timer gestartet: $timerName")
    }

    /** Stoppt einen Timer und sendet die Metrik an Firebase Analytics. */
    fun endTimer(timerName: String) {
        val startTime = timers.remove(timerName)
        if (startTime != null) {
            val duration = SystemClock.elapsedRealtime() - startTime
            AppLogger.d("Performance", "Timer beendet: $timerName, Dauer: ${duration}ms")

            // Firebase Analytics Event senden
            AppAnalytics.trackPerformanceMetric(timerName, duration)

            // Metrik für Durchschnittsberechnung speichern
            performanceMetrics.getOrPut(timerName) { mutableListOf() }.add(duration)
        } else {
            AppLogger.w("Performance", "Timer nicht gefunden: $timerName")
        }
    }

    /** Trackt Speichernutzung für einen bestimmten Kontext. */
    fun trackMemoryUsage(context: String) {
        val runtime = Runtime.getRuntime()
        val usedMemory = runtime.totalMemory() - runtime.freeMemory()
        val usedMemoryMB = usedMemory / (1024 * 1024)

        memoryUsage[context] = usedMemoryMB
        AppLogger.d("Performance", "Speichernutzung für $context: ${usedMemoryMB}MB")

        // Firebase Analytics Event senden
        AppAnalytics.trackPerformanceMetric("memory_usage_$context", usedMemoryMB, "MB")
    }

    /** Trackt App-Start-Performance. */
    fun trackAppStart(duration: Long, coldStart: Boolean) {
        AppAnalytics.trackEvent(
            "app_start",
            mapOf("duration_ms" to duration, "cold_start" to coldStart)
        )

        AppLogger.d("Performance", "App Start: ${duration}ms, Cold Start: $coldStart")
    }

    /** Trackt Datenbank-Operationen. */
    fun trackDatabaseOperation(
        operation: String,
        table: String,
        duration: Long? = null,
        success: Boolean,
    ) {
        val eventData =
            mutableMapOf<String, Any>(
                "operation" to operation,
                "table" to table,
                "success" to success
            )
        duration?.let { eventData["duration_ms"] = it }

        AppAnalytics.trackEvent("database_operation", eventData)

        if (duration != null) {
            AppLogger.d(
                "Performance",
                "Database Operation: $operation on $table, Dauer: ${duration}ms, Erfolg: $success"
            )
        } else {
            AppLogger.d(
                "Performance",
                "Database Operation: $operation on $table, Dauer: n/a, Erfolg: $success"
            )
        }
    }
}
