package uz.yalla.client.service.map.request

import kotlinx.serialization.Serializable

@Serializable
data class GetRoutingRequestItem(
    val type: String,
    val lng: Double,
    val lat: Double
)