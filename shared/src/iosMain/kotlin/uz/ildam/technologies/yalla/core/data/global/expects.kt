package uz.ildam.technologies.yalla.core.data.global

import io.ktor.client.HttpClient
import io.ktor.client.engine.darwin.Darwin
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.header
import platform.UIKit.UIDevice
import uz.ildam.technologies.yalla.core.data.global.Constants

actual fun provideHttpClient() = HttpClient(Darwin) {
    defaultRequest {
        url(Constants.BASE_URL)
        header("brand-id", "2")
        header(
            "User-Agent-OS",
            "${UIDevice.currentDevice.systemName()} ${UIDevice.currentDevice.systemVersion}"
        )
        header("Content-Type", "application/json")
        header("Device-Mode", "mobile")
        header("User-Agent-Version", "${Constants.MAJOR}.${Constants.MINOR}.${Constants.INC}")
        header("Device", "client")
    }
}