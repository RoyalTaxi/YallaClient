package uz.yalla.client.service.payment.request

import kotlinx.serialization.Serializable

@Serializable
data class VerifyCardRequest(
    val key: String,
    val confirm_code: String
)