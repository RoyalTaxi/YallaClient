package uz.yalla.client.service.map.request

import kotlinx.serialization.Serializable

@Serializable
data class SearchForAddressRequest(
    val lat: Double,
    val lng: Double,
    val q: String
)