package uz.ildam.technologies.yalla.feature.auth.data.service

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import uz.ildam.technologies.yalla.core.data.exception.safeApiCall
import uz.ildam.technologies.yalla.core.data.response.ApiResponseWrapper
import uz.ildam.technologies.yalla.core.domain.error.DataError
import uz.ildam.technologies.yalla.core.domain.error.Result
import uz.ildam.technologies.yalla.feature.auth.data.request.register.RegisterRequest
import uz.ildam.technologies.yalla.feature.auth.data.response.register.RegisterResponse
import uz.ildam.technologies.yalla.feature.auth.data.url.RegisterUrl

class RegisterApiService(
    private val ktor: HttpClient
) {
    suspend fun register(
        body: RegisterRequest
    ): Result<ApiResponseWrapper<RegisterResponse>, DataError.Network> = safeApiCall {
        ktor.post(RegisterUrl.REGISTER) { setBody(body) }.body()
    }
}