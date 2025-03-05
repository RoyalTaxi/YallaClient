package uz.yalla.client.service.map.response

import kotlinx.serialization.Serializable

@Serializable
data class PolygonResponseItem(
    val address_id: Int?,
    val polygon: List<Polygon>?
) {
    @Serializable
    data class Polygon(
        val lat: Double?,
        val lng: Double?
    )
}