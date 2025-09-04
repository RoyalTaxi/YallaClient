package uz.yalla.client.service.auth.service

import io.ktor.client.HttpClient
import io.ktor.client.request.post
import uz.yalla.client.core.domain.error.DataError
import uz.yalla.client.core.domain.error.Either
import uz.yalla.client.core.service.network.safeApiCall
import uz.yalla.client.service.auth.url.LogoutUrl

class LogoutService(
    private val ktorApi1: HttpClient
) {
    suspend fun logout(): Either<Unit, DataError.Network> = safeApiCall {
        ktorApi1.post(LogoutUrl.LOGOUT)
    }
}
