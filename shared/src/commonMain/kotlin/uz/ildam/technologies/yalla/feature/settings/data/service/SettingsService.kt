package uz.ildam.technologies.yalla.feature.settings.data.service

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import uz.ildam.technologies.yalla.core.data.exception.safeApiCall
import uz.ildam.technologies.yalla.core.data.response.ApiResponseWrapper
import uz.ildam.technologies.yalla.core.domain.error.DataError
import uz.ildam.technologies.yalla.core.domain.error.Either
import uz.ildam.technologies.yalla.feature.settings.data.response.SettingsResponse
import uz.ildam.technologies.yalla.feature.settings.data.url.SettingsUrl

class SettingsService(
    private val ktor: HttpClient
) {
    suspend fun getConfig(): Either<ApiResponseWrapper<SettingsResponse>, DataError.Network> = safeApiCall {
        ktor.get(SettingsUrl.CONFIG).body()
    }
}