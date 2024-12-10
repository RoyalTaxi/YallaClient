package uz.ildam.technologies.yalla.feature.map.data.response.map

import kotlinx.serialization.Serializable

@Serializable
data class AddressNameResponse(
    val db: Boolean?,
    val display_name: String?,
    val id: Int?,
    val lat: Double?,
    val lng: Double?
)