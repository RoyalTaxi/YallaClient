package uz.ildam.technologies.yalla.feature.auth.data.service

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import uz.ildam.technologies.yalla.core.data.response.ApiResponseWrapper
import uz.ildam.technologies.yalla.feature.auth.data.request.SendAuthCodeRequest
import uz.ildam.technologies.yalla.feature.auth.data.request.ValidateAuthCodeRequest
import uz.ildam.technologies.yalla.feature.auth.data.response.SendAuthCodeResponse
import uz.ildam.technologies.yalla.feature.auth.data.response.ValidateAuthCodeResponse
import uz.ildam.technologies.yalla.feature.auth.data.url.AuthUrl

class AuthApiService(
    private val ktor: HttpClient
) {

    suspend fun sendAuthCode(body: SendAuthCodeRequest): ApiResponseWrapper<SendAuthCodeResponse> {
        return ktor.post(AuthUrl.SEND_SMS) { setBody(body) }.body()
    }

    suspend fun validateAuthCode(body: ValidateAuthCodeRequest): ApiResponseWrapper<ValidateAuthCodeResponse> {
        return ktor.post(AuthUrl.VALIDATE_CODE) { setBody(body) }.body()
    }
}