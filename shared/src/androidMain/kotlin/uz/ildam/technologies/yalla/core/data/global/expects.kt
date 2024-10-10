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

actual fun provideHttpClient() = HttpClient(Android) {
    install(InspektifyKtor)
    defaultRequest {
        url(Constants.BASE_URL)
        header("brand-id", "2")
        header("User-Agent-OS", "${Build.MODEL} ${Build.VERSION.SDK_INT}")
        header("Content-Type", "application/json")
        header("Device-Mode", "mobile")
        header(
            "User-Agent-Version",
            "${Constants.MAJOR}.${Constants.MINOR}.${Constants.INC}"
        )
        header("Device", "client")
        header("secret-key", "227da29a-b4b0-4682-a74f-492466836b6e")
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
