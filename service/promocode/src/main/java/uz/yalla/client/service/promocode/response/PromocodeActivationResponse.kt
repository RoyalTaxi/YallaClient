package uz.yalla.client.service.promocode.response

import kotlinx.serialization.Serializable

@Serializable
data class PromocodeActivationResponse(
    val amount: Int,
    val message: String
)