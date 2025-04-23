package uz.yalla.client.core.data.network

import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.header
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import sp.bvantur.inspektify.ktor.InspektifyKtor
import uz.yalla.client.core.data.local.AppPreferences

fun provideNetworkClient(baseUrl: String) = HttpClient(Android) {
    install(InspektifyKtor)
    defaultRequest {
        url(baseUrl)
        header("lang", AppPreferences.locale)
        header("brand-id", "2")
        header("User-Agent-OS", "android")
        header("Content-Type", "application/json")
        header("Device-Mode", "mobile")
        header("Device", "client")
        header("secret-key", "2f52434c-3068-460d-8dbc-5c80599f2db4")
        header("Authorization", AppPreferences.tokenType + AppPreferences.accessToken)
    }

    install(ContentNegotiation) {
        json(
            Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
                explicitNulls = false
                encodeDefaults = true
            }
        )
    }
}
