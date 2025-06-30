package de.syntax_institut.androidabschlussprojekt.utils

import android.util.Log

object Analytics {
    fun trackEvent(eventName: String, parameters: Map<String, Any> = emptyMap()) {
        Log.d("Analytics", "Event: $eventName, Parameters: $parameters")
        // Hier würde die echte Analytics-Implementation stehen
    }
    
    fun trackScreenView(screenName: String) {
        Log.d("Analytics", "Screen View: $screenName")
    }
    
    fun trackError(error: String, context: String) {
        Log.e("Analytics", "Error in $context: $error")
    }
    
    fun trackUserAction(action: String, gameId: Int? = null) {
        val params = if (gameId != null) {
            mapOf("game_id" to gameId)
        } else {
            emptyMap()
        }
        trackEvent("user_action_$action", params)
    }
}

object PerformanceMonitor {
    private val startTimes = mutableMapOf<String, Long>()
    
    fun startTimer(timerName: String) {
        startTimes[timerName] = System.currentTimeMillis()
    }
    
    fun endTimer(timerName: String): Long {
        val startTime = startTimes[timerName] ?: return 0L
        val duration = System.currentTimeMillis() - startTime
        Log.d("Performance", "$timerName took "+duration+"ms")
        startTimes.remove(timerName)
        return duration
    }
    
    fun trackMemoryUsage(context: String) {
        val runtime = Runtime.getRuntime()
        val usedMemory = runtime.totalMemory() - runtime.freeMemory()
        val maxMemory = runtime.maxMemory()
        val memoryUsage = (usedMemory.toFloat() / maxMemory.toFloat()) * 100
        
        Log.d("Performance", "Memory usage in $context: "+memoryUsage.toInt()+"%")
    }
} 