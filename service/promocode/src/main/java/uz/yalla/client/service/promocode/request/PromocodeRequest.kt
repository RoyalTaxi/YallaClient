package uz.yalla.client.service.promocode.request

import kotlinx.serialization.Serializable

@Serializable
data class PromocodeRequest(
    val value: String
)