package uz.ildam.technologies.yalla.feature.map.data.request.map

import kotlinx.serialization.Serializable

@Serializable
data class SearchForAddressRequest(
    val lat: Double,
    val lng: Double,
    val q: String
)