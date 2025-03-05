package uz.yalla.client.service.auth.request.auth

import kotlinx.serialization.Serializable

@Serializable
data class SendAuthCodeRequest(
    val phone: String,
    val signature: String?
)
