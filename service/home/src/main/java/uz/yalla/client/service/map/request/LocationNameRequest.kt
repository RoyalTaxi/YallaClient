package uz.yalla.client.service.map.request

import kotlinx.serialization.Serializable

@Serializable
data class LocationNameRequest(
    val lat: Double,
    val lng: Double
)