package de.syntax_institut.androidabschlussprojekt.utils

import android.os.*
import java.util.concurrent.*
import java.util.concurrent.atomic.*

/**
 * Performance Monitor für detaillierte Performance-Tracking mit Firebase Analytics. Folgt Clean
 * Code Best Practices: Single Responsibility, DRY, KISS.
 */
object PerformanceMonitor {
    private val timers = ConcurrentHashMap<String, Long>()
    private val memoryUsage = ConcurrentHashMap<String, Long>()
    private val eventCounters = ConcurrentHashMap<String, AtomicLong>()
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

    /** Erhöht einen Event-Counter und sendet ihn an Firebase Analytics. */
    fun incrementEventCounter(eventName: String) {
        val counter = eventCounters.getOrPut(eventName) { AtomicLong(0) }
        val newValue = counter.incrementAndGet()

        AppLogger.d("Performance", "Event Counter erhöht: $eventName = $newValue")

        // Alle 10 Events an Firebase senden
        if (newValue % 10 == 0L) {
            AppAnalytics.trackEvent(
                "event_counter",
                mapOf("event_name" to eventName, "count" to newValue)
            )
        }
    }

    /** Berechnet und sendet Durchschnitts-Performance-Metriken. */
    fun calculateAndSendAverageMetrics() {
        performanceMetrics.forEach { (metricName, values) ->
            if (values.isNotEmpty()) {
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
    fun trackDatabaseOperation(operation: String, table: String, duration: Long, success: Boolean) {
        AppAnalytics.trackEvent(
            "database_operation",
            mapOf(
                "operation" to operation,
                "table" to table,
                "duration_ms" to duration,
                "success" to success
            )
        )

        AppLogger.d(
            "Performance",
            "DB Operation: $operation auf $table, Dauer: ${duration}ms, Erfolg: $success"
        )
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

    /** Bereinigt alte Metriken und sendet Zusammenfassung. */
    fun cleanupAndSendSummary() {
        // Durchschnittsmetriken senden
        calculateAndSendAverageMetrics()

        // Event-Counter senden
        eventCounters.forEach { (eventName, counter) ->
            AppAnalytics.trackEvent(
                "event_summary",
                mapOf("event_name" to eventName, "total_count" to counter.get())
            )
        }

        // Speichernutzung senden
        memoryUsage.forEach { (context, usage) ->
            AppAnalytics.trackPerformanceMetric("final_memory_usage_$context", usage, "MB")
        }

        // Daten bereinigen
        performanceMetrics.clear()
        eventCounters.clear()
        memoryUsage.clear()

        AppLogger.d(
            "Performance",
            "Performance-Monitoring-Daten bereinigt und Zusammenfassung gesendet"
        )
    }

    /** Gibt aktuelle Performance-Statistiken zurück. */
    fun getPerformanceStats(): Map<String, Any> {
        val stats = mutableMapOf<String, Any>()

        // Timer-Statistiken
        stats["active_timers"] = timers.size

        // Memory-Statistiken
        stats["memory_usage"] = memoryUsage.toMap()

        // Event-Counter-Statistiken
        stats["event_counters"] = eventCounters.mapValues { it.value.get() }

        // Performance-Metriken-Statistiken
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
