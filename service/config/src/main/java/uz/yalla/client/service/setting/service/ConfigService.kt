package uz.yalla.client.service.setting.service

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import uz.yalla.client.core.domain.error.DataError
import uz.yalla.client.core.domain.error.Either
import uz.yalla.client.core.service.model.ApiResponseWrapper
import uz.yalla.client.core.service.network.safeApiCall
import uz.yalla.client.service.setting.request.SendFCMTokenRequest
import uz.yalla.client.service.setting.response.ConfigResponse
import uz.yalla.client.service.setting.url.ConfigUrl

class ConfigService(
    private val ktorApi1: HttpClient,
    private val ktorApi2: HttpClient
) {
    suspend fun getConfig(): Either<ApiResponseWrapper<ConfigResponse>, DataError.Network> =
        safeApiCall(isIdempotent = true) {
            ktorApi2.get(ConfigUrl.CONFIG)
        }

    suspend fun sendFCMToken(body: SendFCMTokenRequest): Either<Unit, DataError.Network> =
        safeApiCall {
            ktorApi1.post(ConfigUrl.FCM_TOKEN) { setBody(body) }
        }
}
