package uz.yalla.client.feature.map.domain.model.response


data class PolygonRemoteItem(
    val addressId: Int,
    val polygons: List<Polygon>
) {
    data class Polygon(
        val lat: Double,
        val lng: Double
    )
}