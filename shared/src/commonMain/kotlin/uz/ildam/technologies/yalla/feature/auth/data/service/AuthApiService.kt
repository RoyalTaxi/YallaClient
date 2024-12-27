package uz.ildam.technologies.yalla.feature.auth.data.service

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import uz.ildam.technologies.yalla.core.data.exception.safeApiCall
import uz.ildam.technologies.yalla.core.data.response.ApiResponseWrapper
import uz.ildam.technologies.yalla.core.domain.error.DataError
import uz.ildam.technologies.yalla.core.domain.error.Either
import uz.ildam.technologies.yalla.feature.auth.data.request.auth.SendAuthCodeRequest
import uz.ildam.technologies.yalla.feature.auth.data.request.auth.ValidateAuthCodeRequest
import uz.ildam.technologies.yalla.feature.auth.data.response.auth.SendAuthCodeResponse
import uz.ildam.technologies.yalla.feature.auth.data.response.auth.ValidateAuthCodeResponse
import uz.ildam.technologies.yalla.feature.auth.data.url.AuthUrl

class AuthApiService(
    private val ktor: HttpClient
) {

    suspend fun sendAuthCode(
        body: SendAuthCodeRequest
    ): Either<ApiResponseWrapper<SendAuthCodeResponse>, DataError.Network> = safeApiCall {
        ktor.post(AuthUrl.SEND_SMS) { setBody(body) }.body()
    }

    suspend fun validateAuthCode(
        body: ValidateAuthCodeRequest
    ): Either<ApiResponseWrapper<ValidateAuthCodeResponse>, DataError.Network> = safeApiCall {
        ktor.post(AuthUrl.VALIDATE_CODE) { setBody(body) }.body()
    }
}