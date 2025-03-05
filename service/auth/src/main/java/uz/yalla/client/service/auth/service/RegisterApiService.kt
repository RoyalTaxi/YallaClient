package uz.yalla.client.service.auth.service

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import uz.yalla.client.service.auth.url.RegisterUrl
import uz.yalla.client.core.domain.error.DataError
import uz.yalla.client.core.domain.error.Either
import uz.yalla.client.core.service.model.ApiResponseWrapper
import uz.yalla.client.core.service.network.safeApiCall
import uz.yalla.client.service.auth.request.register.RegisterUserRequest
import uz.yalla.client.service.auth.response.register.RegisterResponse

class RegisterApiService(
    private val ktor: HttpClient
) {
    suspend fun register(
        body: RegisterUserRequest
    ): Either<ApiResponseWrapper<RegisterResponse>, DataError.Network> = safeApiCall {
        ktor.post(RegisterUrl.REGISTER) { setBody(body) }.body()
    }
}