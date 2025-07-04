package de.syntax_institut.androidabschlussprojekt.utils

import android.content.*
import android.net.*
import kotlinx.coroutines.channels.*
import kotlinx.coroutines.flow.*

/**
 * Utility-Klasse für Netzwerkstatus-Überprüfung.
 */
object NetworkUtils {
    
    /**
     * Prüft ob eine Internetverbindung verfügbar ist.
     */
    fun isNetworkAvailable(context: Context): Boolean {
        return try {
            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val network = connectivityManager.activeNetwork ?: return false
            val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false

            activeNetwork.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                    activeNetwork.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
        } catch (e: Exception) {
            // Fallback auf false bei Fehlern
            false
        }
    }
    
    /**
     * Flow für Netzwerkstatus-Änderungen.
     */
    fun observeNetworkStatus(context: Context): Flow<Boolean> = callbackFlow {
        val connectivityManager = try {
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        } catch (e: Exception) {
            // Fallback auf false bei Fehlern
            trySend(false)
            close()
            return@callbackFlow
        }
        
        val networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                try {
                    trySend(true)
                } catch (e: Exception) {
                    // Ignoriere Fehler beim Senden
                }
            }
            
            override fun onLost(network: Network) {
                try {
                    trySend(false)
                } catch (e: Exception) {
                    // Ignoriere Fehler beim Senden
                }
            }
            
            override fun onCapabilitiesChanged(
                network: Network,
                networkCapabilities: NetworkCapabilities
            ) {
                try {
                    val isConnected =
                        networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                                networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
                    trySend(isConnected)
                } catch (e: Exception) {
                    // Ignoriere Fehler beim Senden
                }
            }
        }
        
        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        try {
            connectivityManager.registerNetworkCallback(networkRequest, networkCallback)

            // Initialer Status
            trySend(isNetworkAvailable(context))
        } catch (e: Exception) {
            // Fallback auf false bei Registrierungsfehlern
            trySend(false)
        }
        
        awaitClose {
            try {
                connectivityManager.unregisterNetworkCallback(networkCallback)
            } catch (e: Exception) {
                // Ignoriere Fehler beim Aufheben der Registrierung
            }
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