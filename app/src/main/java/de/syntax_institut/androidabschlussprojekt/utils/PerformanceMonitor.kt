package de.syntax_institut.androidabschlussprojekt.utils

import android.os.*
import de.syntax_institut.androidabschlussprojekt.data.*
import java.util.concurrent.atomic.*

/**
 * Performance Monitor für detailliertes Performance-Tracking mit Firebase Analytics.
 * - Unterstützt Timer, Memory, Event-Counter, Durchschnittsmetriken, API-, Image-, UI-, Cache-,
 * Netzwerk- und Navigations-Tracking.
 * - Clean Code: Single Responsibility, DRY, KISS, KDoc.
 */
object PerformanceMonitor {
    private val timers = mutableMapOf<String, Long>()
    private val memoryUsage = mutableMapOf<String, Long>()
    private val eventCounters = mutableMapOf<String, AtomicLong>()
    private val performanceMetrics = mutableMapOf<String, MutableList<Long>>()

    /**
     * Startet einen Timer für Performance-Messungen.
     * @param timerName Name des Timers
     */
    fun startTimer(timerName: String) {
        timers[timerName] = SystemClock.elapsedRealtime()
        AppLogger.d("Performance", "Timer gestartet: $timerName")
    }

    /**
     * Beendet einen Timer und trackt die Dauer.
     * @param timerName Name des Timers
     * @return Die verstrichene Zeit in Millisekunden
     */
    fun endTimer(timerName: String): Long {
        val startTime = timers.remove(timerName) ?: return 0L
        val duration = SystemClock.elapsedRealtime() - startTime

        // Performance-Metriken sammeln
        performanceMetrics.getOrPut(timerName) { mutableListOf() }.add(duration)

        // Automatische Fehleraufzeichnung bei langsamen Operationen
        if (duration > Constants.PERFORMANCE_THRESHOLD_MS) {
            CrashlyticsHelper.recordPerformanceError(
                timerName,
                duration,
                Constants.PERFORMANCE_THRESHOLD_MS
            )
        }

        AppLogger.d("Performance", "Timer beendet: $timerName, Dauer: ${duration}ms")
        return duration
    }

    /**
     * Trackt Memory-Nutzung für einen Kontext.
     * @param contextName Name des Kontexts
     */
    fun trackMemoryUsage(contextName: String) {
        val runtime = Runtime.getRuntime()
        val usedMemory = runtime.totalMemory() - runtime.freeMemory()
        memoryUsage[contextName] = usedMemory

        // Automatische Fehleraufzeichnung bei hoher Memory-Nutzung
        if (usedMemory > Constants.MEMORY_WARNING_THRESHOLD_MB * 1024 * 1024) {
            CrashlyticsHelper.recordPerformanceError(
                "memory_usage",
                usedMemory / (1024 * 1024),
                Constants.MEMORY_WARNING_THRESHOLD_MB
            )
        }

        AppLogger.d("Performance", "Memory Usage: $contextName = ${usedMemory / (1024 * 1024)}MB")
    }

    /**
     * Erhöht einen Event-Counter und trackt ihn.
     * @param eventName Name des Events
     */
    fun incrementEventCounter(eventName: String) {
        val counter = eventCounters.getOrPut(eventName) { AtomicLong(0) }
        val newValue = counter.incrementAndGet()
        AppLogger.d("Performance", "Event Counter erhöht: $eventName = $newValue")
        if (newValue % 10 == 0L) {
            AppAnalytics.trackEvent(
                "event_counter",
                mapOf("event_name" to eventName, "count" to newValue)
            )
        }
    }

    /** Berechnet und sendet Durchschnittsmetriken für alle gesammelten Daten. */
    private fun calculateAndSendAverageMetrics() {
        performanceMetrics.forEach { (metricName, values) ->
            if (values.size >= 5) { // Mindestens 5 Samples
                val average = values.average().toLong()
                val min = values.minOrNull() ?: 0L
                val max = values.maxOrNull() ?: 0L
                AppAnalytics.trackEvent(
                    "performance_average",
                    mapOf(
                        "metric_name" to metricName,
                        "average_ms" to average,
                        "min_ms" to min,
                        "max_ms" to max,
                        "sample_count" to values.size
                    )
                )
                AppLogger.d(
                    "Performance",
                    "Durchschnitt für $metricName: ${average}ms (Min: ${min}ms, Max: ${max}ms, Samples: ${values.size})"
                )
            }
        }
    }

    /** Trackt API-Aufrufe und deren Performance. */
    fun trackApiCall(endpoint: String, duration: Long, success: Boolean, responseSize: Int = 0) {
        AppAnalytics.trackEvent(
            "api_call",
            mapOf(
                "endpoint" to endpoint,
                "duration_ms" to duration,
                "success" to success,
                "response_size_bytes" to responseSize
            )
        )
        AppLogger.d("Performance", "API Call: $endpoint, Dauer: ${duration}ms, Erfolg: $success")
    }

    /** Trackt Bildlade-Performance. */
    fun trackImageLoad(imageUrl: String, duration: Long, success: Boolean, imageSize: Int = 0) {
        AppAnalytics.trackEvent(
            "image_load",
            mapOf(
                "image_url" to imageUrl,
                "duration_ms" to duration,
                "success" to success,
                "image_size_bytes" to imageSize
            )
        )
        AppLogger.d("Performance", "Image Load: $imageUrl, Dauer: ${duration}ms, Erfolg: $success")
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

    /** Trackt UI-Rendering-Performance. */
    fun trackUiRendering(screenName: String, duration: Long) {
        AppAnalytics.trackEvent(
            "ui_rendering",
            mapOf("screen_name" to screenName, "duration_ms" to duration)
        )
        AppLogger.d("Performance", "UI Rendering: $screenName, Dauer: ${duration}ms")
    }

    /** Trackt App-Start-Performance. */
    fun trackAppStart(duration: Long, coldStart: Boolean) {
        AppAnalytics.trackEvent(
            "app_start",
            mapOf("duration_ms" to duration, "cold_start" to coldStart)
        )
        AppLogger.d("Performance", "App Start: ${duration}ms, Cold Start: $coldStart")
    }

    /** Trackt Navigation-Performance. */
    fun trackNavigation(fromScreen: String, toScreen: String, duration: Long) {
        AppAnalytics.trackEvent(
            "navigation",
            mapOf(
                "from_screen" to fromScreen,
                "to_screen" to toScreen,
                "duration_ms" to duration
            )
        )
        AppLogger.d("Performance", "Navigation: $fromScreen -> $toScreen, Dauer: ${duration}ms")
    }

    /** Trackt Cache-Performance. */
    fun trackCachePerformance(operation: String, cacheSize: Int, hitRate: Float, duration: Long) {
        AppAnalytics.trackEvent(
            "cache_performance",
            mapOf(
                "operation" to operation,
                "cache_size" to cacheSize,
                "hit_rate" to hitRate,
                "duration_ms" to duration
            )
        )
        AppLogger.d(
            "Performance",
            "Cache: $operation, Größe: $cacheSize, Hit Rate: $hitRate, Dauer: ${duration}ms"
        )
    }

    /** Trackt Netzwerk-Performance. */
    fun trackNetworkPerformance(
        requestType: String,
        duration: Long,
        bytesTransferred: Long,
        success: Boolean,
    ) {
        AppAnalytics.trackEvent(
            "network_performance",
            mapOf(
                "request_type" to requestType,
                "duration_ms" to duration,
                "bytes_transferred" to bytesTransferred,
                "success" to success
            )
        )
        AppLogger.d(
            "Performance",
            "Network: $requestType, Dauer: ${duration}ms, Bytes: $bytesTransferred, Erfolg: $success"
        )
    }

    /**
     * Bereinigt alle gesammelten Daten und sendet eine Zusammenfassung. Sollte regelmäßig
     * aufgerufen werden, um Memory zu sparen.
     */
    fun cleanupAndSendSummary() {
        calculateAndSendAverageMetrics()
        eventCounters.forEach { (eventName, counter) ->
            AppAnalytics.trackEvent(
                "event_summary",
                mapOf("event_name" to eventName, "total_count" to counter.get())
            )
        }
        memoryUsage.forEach { (context, usage) ->
            AppAnalytics.trackEvent(
                "memory_summary",
                mapOf("context" to context, "usage_mb" to (usage / (1024 * 1024)))
            )
        }
        timers.clear()
        performanceMetrics.clear()
        eventCounters.clear()
        memoryUsage.clear()
        AppLogger.d(
            "Performance",
            "Performance-Monitoring-Daten bereinigt und Zusammenfassung gesendet"
        )
    }

    /**
     * Gibt alle gesammelten Performance-Statistiken zurück.
     * @return Map mit allen Performance-Daten
     */
    fun getPerformanceStats(): Map<String, Any> {
        val stats = mutableMapOf<String, Any>()
        stats["active_timers"] = timers.keys.toList()
        stats["memory_usage"] = memoryUsage.toMap()
        stats["event_counters"] = eventCounters.mapValues { it.value.get() }
        stats["performance_metrics"] =
            performanceMetrics.mapValues { (_, values) ->
                mapOf(
                    "count" to values.size,
                    "average" to values.average(),
                    "min" to values.minOrNull(),
                    "max" to values.maxOrNull()
                )
            }
        return stats
    }
}
