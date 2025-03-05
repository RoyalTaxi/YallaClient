package uz.yalla.client.feature.map.domain.model.response

data class GetRoutingModel(
    val distance: Double,
    val duration: Double,
    val routing: List<Routing>
) {
    data class Routing(
        val lat: Double,
        val lng: Double
    )
}