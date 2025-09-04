package uz.yalla.client.feature.map.domain.model.request

data class GetRoutingDtoItem(
    val type: String,
    val lng: Double,
    val lat: Double
) {
    companion object {
        const val START = "start"
        const val POINT = "point"
        // Backend expects the last point labelled as "stop"
        const val STOP = "stop"
    }
}
