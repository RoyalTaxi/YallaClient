package uz.ildam.technologies.yalla.feature.order.domain.model.map


data class PolygonRemoteItem(
    val addressId: Int?,
    val polygons: List<Polygon>
) {
    data class Polygon(
        val lat: Double,
        val lng: Double
    )
}