package uz.yalla.client.service.payment.request

import kotlinx.serialization.Serializable

@Serializable
data class AddCardRequest(
    val number: String,
    val expiry: String
)