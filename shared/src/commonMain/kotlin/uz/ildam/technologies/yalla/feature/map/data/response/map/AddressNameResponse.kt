package uz.ildam.technologies.yalla.feature.map.data.response.map

import kotlinx.serialization.Serializable

@Serializable
data class AddressNameResponse(
    val lat: String?,
    val lng: String?,
    val name: String?
)