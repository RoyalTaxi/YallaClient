package uz.yalla.client.service.map.response

import kotlinx.serialization.Serializable

@Serializable
data class PlaceNameResponse(
    val db: Boolean?,
    val display_name: String?,
    val id: Int?,
    val lat: Double?,
    val lng: Double?
)