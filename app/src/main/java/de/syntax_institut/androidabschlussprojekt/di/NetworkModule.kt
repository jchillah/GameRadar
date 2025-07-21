package de.syntax_institut.androidabschlussprojekt.di

import de.syntax_institut.androidabschlussprojekt.*
import de.syntax_institut.androidabschlussprojekt.data.*
import de.syntax_institut.androidabschlussprojekt.data.remote.*
import de.syntax_institut.androidabschlussprojekt.utils.*
import okhttp3.*
import okhttp3.logging.*
import org.koin.dsl.*
import retrofit2.*
import retrofit2.converter.moshi.*
import java.util.concurrent.*

/**
 * Dependency Injection Modul für Netzwerk-Komponenten.
 *
 * Stellt alle Netzwerk-abhängigen Services bereit:
 * - OkHttp Client mit Interceptors
 * - Retrofit Instance für RAWG API
 * - API-Interface
 * - Performance-Monitoring Interceptor
 */
val networkModule = module {
    single { provideOkHttpClient() }
    single { provideRetrofit(get()) }
    single { provideRawgApi(get()) }
}

/**
 * Erstellt einen OkHttp Client mit Interceptors für Logging und Performance-Monitoring.
 *
 * @return Konfigurierter OkHttp Client
 */
private fun provideOkHttpClient(): OkHttpClient {
    return OkHttpClient.Builder()
        .addInterceptor(PerformanceMonitoringInterceptor())
        .addInterceptor(
            HttpLoggingInterceptor().apply {
                level =
                    if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY
                    else HttpLoggingInterceptor.Level.NONE
            }
        )
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()
}

/**
 * Erstellt eine Retrofit Instance für die RAWG API.
 *
 * @param okHttpClient Der konfigurierte OkHttp Client
 * @return Retrofit Instance
 */
private fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
    return Retrofit.Builder()
        .baseUrl(Constants.BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create(MoshiProvider.moshi))
        .build()
}

/**
 * Erstellt das RAWG API Interface.
 *
 * @param retrofit Die Retrofit Instance
 * @return RawgApi Interface
 */
private fun provideRawgApi(retrofit: Retrofit): RawgApi {
    return retrofit.create(RawgApi::class.java)
}

/**
 * OkHttp Interceptor für Performance-Monitoring aller API-Aufrufe.
 *
 * Trackt automatisch:
 * - Request-Dauer
 * - Response-Größe
 * - Erfolg/Fehler-Status
 * - Endpoint-spezifische Metriken
 */
class PerformanceMonitoringInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
        val request = chain.request()
        val startTime = System.currentTimeMillis()

        // Extrahiere Endpoint-Informationen
        val endpoint = request.url.encodedPath
        val method = request.method

        try {
            val response = chain.proceed(request)
            val endTime = System.currentTimeMillis()
            val duration = endTime - startTime

            // Berechne Response-Größe
            val responseSize = response.body?.contentLength()?.toInt() ?: 0

            // Performance-Tracking
            PerformanceMonitor.trackApiCall(
                endpoint,
                duration,
                response.isSuccessful,
                responseSize
            )
            PerformanceMonitor.trackNetworkPerformance(
                method,
                duration,
                responseSize.toLong(),
                response.isSuccessful
            )

            // Event-Counter für spezifische Endpoints
            when {
                endpoint.contains("/games") ->
                    PerformanceMonitor.incrementEventCounter("games_api_calls")

                endpoint.contains("/platforms") ->
                    PerformanceMonitor.incrementEventCounter(
                        "platforms_api_calls"
                    )

                endpoint.contains("/genres") ->
                    PerformanceMonitor.incrementEventCounter("genres_api_calls")

                endpoint.contains("/screenshots") ->
                    PerformanceMonitor.incrementEventCounter(
                        "screenshots_api_calls"
                    )

                endpoint.contains("/movies") ->
                    PerformanceMonitor.incrementEventCounter("movies_api_calls")
            }

            return response
        } catch (e: Exception) {
            val endTime = System.currentTimeMillis()
            val duration = endTime - startTime

            // Error-Tracking
            PerformanceMonitor.trackApiCall(endpoint, duration, false, 0)
            PerformanceMonitor.trackNetworkPerformance(method, duration, 0L, false)

            // Crashlytics Error Recording
            CrashlyticsHelper.recordApiError(
                endpoint,
                0, // errorCode als Int
                e.message ?: "Unknown error"
            )

            throw e
        }
    }
}
