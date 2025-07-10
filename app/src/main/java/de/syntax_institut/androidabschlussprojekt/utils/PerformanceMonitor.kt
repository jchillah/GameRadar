package de.syntax_institut.androidabschlussprojekt.utils

object PerformanceMonitor {
    private val startTimes = mutableMapOf<String, Long>()

    fun startTimer(timerName: String) {
        startTimes[timerName] = System.currentTimeMillis()
    }

    fun endTimer(timerName: String): Long {
        val startTime = startTimes[timerName] ?: return 0L
        val duration = System.currentTimeMillis() - startTime
        AppLogger.d("PerformanceMonitor", "$timerName took ${duration}ms")
        startTimes.remove(timerName)
        return duration
    }

    fun trackMemoryUsage(context: String) {
        val runtime = Runtime.getRuntime()
        val usedMemory = runtime.totalMemory() - runtime.freeMemory()
        val maxMemory = runtime.maxMemory()
        val memoryUsage = (usedMemory.toFloat() / maxMemory.toFloat()) * 100

        AppLogger.d("PerformanceMonitor", "Memory usage in $context: ${memoryUsage.toInt()}%")
    }
}