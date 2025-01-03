package uz.ildam.technologies.yalla.feature.map.data.request.map

import kotlinx.serialization.Serializable

@Serializable
data class GetRoutingRequestItem(
    val type: String,
    val lng: Double,
    val lat: Double
)