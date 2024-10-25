package uz.ildam.technologies.yalla.feature.order.data.request.map

import kotlinx.serialization.Serializable

@Serializable
data class PolygonRequest(
    val lat: Double,
    val lng: Double
)
