package uz.yalla.client.service.auth.response.auth

import kotlinx.serialization.Serializable

@Serializable
data class SendAuthCodeResponse(
    val time: Int?,
    val result_message: String?
)