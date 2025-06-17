package de.syntax_institut.androidabschlussprojekt.di

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import de.syntax_institut.androidabschlussprojekt.data.remote.RawgApi
import java.util.concurrent.TimeUnit

val networkModule = module {
    single {
        // Baue Moshi mit Kotlin-Adapter
        Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    single {
        // Logging-Interceptor für Debug
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    single {
        // Retrofit mit MoshiConverterFactory.create(moshi)
        Retrofit.Builder()
            .baseUrl("https://api.rawg.io/api/")
            .client(get())
            .addConverterFactory(MoshiConverterFactory.create(get()))
            .build()
            .create(RawgApi::class.java)
    }
}
