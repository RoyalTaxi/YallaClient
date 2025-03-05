package uz.yalla.client.service.payment.response

import kotlinx.serialization.Serializable

@Serializable
data class AddCardResponse(
    val expiry: String?,
    val key: String?,
    val number: String?,
    val phone: String?
)