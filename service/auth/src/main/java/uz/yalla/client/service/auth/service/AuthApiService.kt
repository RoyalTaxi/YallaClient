package uz.yalla.client.service.auth.service

import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import uz.yalla.client.service.auth.response.auth.ValidateAuthCodeResponse
import uz.yalla.client.service.auth.url.AuthUrl
import uz.yalla.client.core.domain.error.DataError
import uz.yalla.client.core.domain.error.Either
import uz.yalla.client.core.service.model.ApiResponseWrapper
import uz.yalla.client.core.service.network.safeApiCall
import uz.yalla.client.service.auth.request.auth.SendAuthCodeRequest
import uz.yalla.client.service.auth.request.auth.ValidateAuthCodeRequest
import uz.yalla.client.service.auth.response.auth.SendAuthCodeResponse

class AuthApiService(
    private val ktor: HttpClient
) {
    suspend fun sendAuthCode(
        body: SendAuthCodeRequest
    ): Either<ApiResponseWrapper<SendAuthCodeResponse>, DataError.Network> = safeApiCall {
        ktor.post(AuthUrl.SEND_SMS) { setBody(body) }
    }

    suspend fun validateAuthCode(
        body: ValidateAuthCodeRequest
    ): Either<ApiResponseWrapper<ValidateAuthCodeResponse>, DataError.Network> = safeApiCall {
        ktor.post(AuthUrl.VALIDATE_CODE) { setBody(body) }
    }
}
