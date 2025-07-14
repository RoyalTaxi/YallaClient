package uz.yalla.client.core.data.network

import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.header
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import sp.bvantur.inspektify.ktor.InspektifyKtor
import uz.yalla.client.core.data.BuildConfig
import uz.yalla.client.core.domain.local.AppPreferences

private val localeCache = MutableStateFlow("")
private val accessTokenCache = MutableStateFlow("")

private val cacheScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

fun provideNetworkClient(
    baseUrl: String,
    preferences: AppPreferences
): HttpClient {
    cacheScope.launch {
        localeCache.value = preferences.locale.last()
        accessTokenCache.value = preferences.accessToken.last()
    }

    cacheScope.launch {
        preferences.locale.collectLatest { localeCache.value = it }
    }
    cacheScope.launch {
        preferences.accessToken.collectLatest { accessTokenCache.value = it }
    }

    return HttpClient(Android) {
        if (BuildConfig.DEBUG) {
            install(InspektifyKtor) {
                shortcutEnabled = true
            }
        }

        defaultRequest {
            url(baseUrl)
            header("lang", localeCache.value)
            header("brand-id", "2")
            header("User-Agent-OS", "android")
            header("Content-Type", "application/json")
            header("Device-Mode", "mobile")
            header("Device", "client")
            header("secret-key", "227da29a-b4b0-4682-a74f-492466836b6e")
            header(
                "Authorization",
                "Bearer " + accessTokenCache.value
            )
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
}