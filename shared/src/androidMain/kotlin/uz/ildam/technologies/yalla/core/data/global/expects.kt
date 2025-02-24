package uz.ildam.technologies.yalla.core.data.global

import android.os.Build
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.header
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import sp.bvantur.inspektify.ktor.InspektifyKtor
import uz.ildam.technologies.yalla.core.data.local.AppPreferences

actual fun provideHttpClientForApi1() = HttpClient(Android) {
    install(InspektifyKtor)
    defaultRequest {
        url(Constants.BASE_URL)
        header("lang", AppPreferences.locale)
        header("brand-id", "2")
        header("User-Agent-OS", "${Build.MODEL} ${Build.VERSION.SDK_INT}")
        header("Content-Type", "application/json")
        header("Device-Mode", "mobile")
        header(
            "User-Agent-Version",
            "${Constants.MAJOR}.${Constants.MINOR}.${Constants.INC}"
        )
        header("Device", "client")
        header("secret-key", "2f52434c-3068-460d-8dbc-5c80599f2db4")
        header("Authorization", AppPreferences.tokenType + AppPreferences.accessToken)
    }

    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true
            explicitNulls = false
        })
    }
}

actual fun provideHttpClientForApi2() = HttpClient(Android) {
    install(InspektifyKtor)
    defaultRequest {
        url(Constants.BASE_URL_2)
        header("lang", AppPreferences.locale)
        header("brand-id", "2")
        header("User-Agent-OS", "${Build.MODEL} ${Build.VERSION.SDK_INT}")
        header("Content-Type", "application/json")
        header("Device-Mode", "mobile")
        header(
            "User-Agent-Version",
            "${Constants.MAJOR}.${Constants.MINOR}.${Constants.INC}"
        )
        header("Device", "client")
        header("secret-key", "2f52434c-3068-460d-8dbc-5c80599f2db4")
        header("Authorization", AppPreferences.tokenType + AppPreferences.accessToken)
    }

    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true
            explicitNulls = false
        })
    }
}