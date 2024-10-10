package uz.ildam.technologies.yalla.feature.auth.data.service

import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import uz.ildam.technologies.yalla.feature.auth.data.request.register.RegisterRequest
import uz.ildam.technologies.yalla.feature.auth.data.url.RegisterUrl

class RegisterApiService(
    private val ktor: HttpClient
) {

    suspend fun register(body: RegisterRequest) {
        ktor.post(RegisterUrl.REGISTER) { setBody(body) }
    }
}