package uz.yalla.client.feature.home.domain.model.response


data class PolygonRemoteItem(
    val addressId: Int,
    val polygons: List<Polygon>
) {
    data class Polygon(
        val lat: Double,
        val lng: Double
    )
}