package uz.yalla.client.service.auth.request.auth

import kotlinx.serialization.Serializable

@Serializable
data class ValidateAuthCodeRequest(
    val phone: String,
    val code: Int
)
