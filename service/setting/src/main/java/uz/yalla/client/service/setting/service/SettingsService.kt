package uz.yalla.client.service.setting.service

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import uz.yalla.client.service.setting.response.SettingsResponse
import uz.yalla.client.service.setting.url.SettingsUrl
import uz.yalla.client.core.domain.error.DataError
import uz.yalla.client.core.domain.error.Either
import uz.yalla.client.core.service.model.ApiResponseWrapper
import uz.yalla.client.core.service.network.safeApiCall
import uz.yalla.client.service.setting.request.SendFCMTokenRequest

class SettingsService(
    private val ktorPhp: HttpClient,
    private val ktorGo: HttpClient
) {
    suspend fun getConfig(): Either<ApiResponseWrapper<SettingsResponse>, DataError.Network> =
        safeApiCall {
            ktorGo.get(SettingsUrl.CONFIG).body()
        }

    suspend fun sendFCMToken(body: SendFCMTokenRequest): Either<Any, DataError.Network> =
        safeApiCall {
            ktorPhp.post(SettingsUrl.FCM_TOKEN) { setBody(body) }.body()
        }
}