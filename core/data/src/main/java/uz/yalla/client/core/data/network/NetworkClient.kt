package uz.yalla.client.core.data.network

import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.HttpCallValidator
import io.ktor.client.plugins.HttpTimeout
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
import io.ktor.http.HttpStatusCode
import io.ktor.client.plugins.ClientRequestException
import uz.yalla.client.core.domain.local.StaticPreferences

private val localeCache = MutableStateFlow("")
private val accessTokenCache = MutableStateFlow("")

private val cacheScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

fun provideNetworkClient(
    baseUrl: String,
    appPrefs: AppPreferences,
    staticPrefs: StaticPreferences
): HttpClient {
    cacheScope.launch {
        localeCache.value = appPrefs.locale.last()
        accessTokenCache.value = appPrefs.accessToken.last()
    }

    cacheScope.launch {
        appPrefs.locale.collectLatest { localeCache.value = it }
    }
    cacheScope.launch {
        appPrefs.accessToken.collectLatest { accessTokenCache.value = it }
    }

    return HttpClient(Android) {
        if (BuildConfig.DEBUG) {
            install(InspektifyKtor) {
                shortcutEnabled = true
            }
        }

        install(HttpCallValidator) {
            validateResponse { response ->
                if (response.status == HttpStatusCode.Unauthorized) {
                    appPrefs.performLogout()
                    staticPrefs.performLogout()                }
            }
            handleResponseExceptionWithRequest { cause, _ ->
                if (cause is ClientRequestException && cause.response.status == HttpStatusCode.Unauthorized) {
                    appPrefs.performLogout()
                    staticPrefs.performLogout()                }
            }
        }

        install(HttpTimeout) {
            requestTimeoutMillis = 15_000
            connectTimeoutMillis = 10_000
            socketTimeoutMillis = 15_000
        }

        defaultRequest {
            url(baseUrl)
            header("lang", localeCache.value)
            header("brand-id", "2")
            header("User-Agent-OS", "android")
            header("Content-Type", "application/json")
            header("Device-Mode", "mobile")
            header("Device", "client")
            header("secret-key", BuildConfig.SECRET_KEY)
            header(
                "Authorization",
                "Bearer " + accessTokenCache.value
            )
        }

        install(ContentNegotiation) {
            json(
                Json {
                    prettyPrint = BuildConfig.DEBUG
                    isLenient = true
                    ignoreUnknownKeys = true
                    explicitNulls = false
                    encodeDefaults = true
                }
            )
        }
    }
}
