package uz.yalla.client.service.payment.response

import kotlinx.serialization.Serializable

@Serializable
data class AddCardResponse(
    val key: String?
)