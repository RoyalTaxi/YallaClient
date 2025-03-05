package uz.yalla.client.service.setting.service

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import uz.yalla.client.service.setting.response.SettingsResponse
import uz.yalla.client.service.settings.url.SettingsUrl
import uz.yalla.client.core.domain.error.DataError
import uz.yalla.client.core.domain.error.Either
import uz.yalla.client.core.service.model.ApiResponseWrapper
import uz.yalla.client.core.service.network.safeApiCall

class SettingsService(
    private val ktor: HttpClient
) {
    suspend fun getConfig(): Either<ApiResponseWrapper<SettingsResponse>, DataError.Network> = safeApiCall {
        ktor.get(SettingsUrl.CONFIG).body()
    }
}