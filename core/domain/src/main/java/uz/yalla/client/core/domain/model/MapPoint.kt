package uz.yalla.client.core.domain.model

data class MapPoint(
    val lat: Double,
    val lng: Double
) {
    companion object {
        val Zero = MapPoint(0.0, 0.0)
    }
}