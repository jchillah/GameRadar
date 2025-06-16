package de.syntax_institut.androidabschlussprojekt

import de.syntax_institut.androidabschlussprojekt.data.remote.RawgApi
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.koin.core.context.startKoin
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class NetworkSmokeTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var api: RawgApi

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody("""
                    {
                        "results": [],
                        "next": null,
                        "previous": null
                    }
                """.trimIndent())
        )

        val testModule = module {
            single {
                Retrofit.Builder()
                    .baseUrl(mockWebServer.url("/"))
                    .addConverterFactory(MoshiConverterFactory.create())
                    .build()
                    .create(RawgApi::class.java)
            }
        }

        startKoin {
            modules(testModule)
        }

        // Hole die Instanz direkt
        api = org.koin.java.KoinJavaComponent.get(RawgApi::class.java)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `test rawg api with mock server`() = runBlocking {
        val response = api.searchGames(query = "")
        assertEquals(true, response.isSuccessful)
    }
}
