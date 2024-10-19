package uz.ildam.technologies.yalla.feature.order.data.response.map

import kotlinx.serialization.Serializable

@Serializable
data class PolygonResponseItem(
    val address_id: Int?,
    val polygons: List<Polygon>?
) {
    @Serializable
    data class Polygon(
        val lat: Double?,
        val lng: Double?
    )
}