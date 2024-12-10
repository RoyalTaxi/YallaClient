package uz.ildam.technologies.yalla.feature.map.data.request.map

import kotlinx.serialization.Serializable

@Serializable
data class LocationNameRequest(
    val lat: Double,
    val lng: Double
)