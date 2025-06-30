package de.syntax_institut.androidabschlussprojekt.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged

/**
 * Utility-Klasse für Netzwerkstatus-Überprüfung.
 */
object NetworkUtils {
    
    /**
     * Prüft ob eine Internetverbindung verfügbar ist.
     */
    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
        
        return activeNetwork.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
               activeNetwork.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }
    
    /**
     * Flow für Netzwerkstatus-Änderungen.
     */
    fun observeNetworkStatus(context: Context): Flow<Boolean> = callbackFlow {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        
        val networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                trySend(true)
            }
            
            override fun onLost(network: Network) {
                trySend(false)
            }
            
            override fun onCapabilitiesChanged(
                network: Network,
                networkCapabilities: NetworkCapabilities
            ) {
                val isConnected = networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                        networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
                trySend(isConnected)
            }
        }
        
        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        
        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)
        
        // Initialer Status
        trySend(isNetworkAvailable(context))
        
        awaitClose {
            connectivityManager.unregisterNetworkCallback(networkCallback)
        }
    }.distinctUntilChanged()
    
    /**
     * Cache-Invalidierungszeit in Millisekunden (24 Stunden).
     */
    const val CACHE_INVALIDATION_TIME = 24 * 60 * 60 * 1000L
    
    /**
     * Prüft ob Cache noch gültig ist.
     */
    fun isCacheValid(cacheTime: Long): Boolean {
        return System.currentTimeMillis() - cacheTime < CACHE_INVALIDATION_TIME
    }
    
    /**
     * Erstellt einen Hash für Filter-Parameter.
     */
    fun createFilterHash(
        platforms: String?,
        genres: String?,
        ordering: String?,
        rating: Float?
    ): String {
        return "${platforms ?: ""}_${genres ?: ""}_${ordering ?: ""}_${rating ?: 0f}".hashCode().toString()
    }
} 