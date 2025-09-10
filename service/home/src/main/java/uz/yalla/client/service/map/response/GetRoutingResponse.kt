package uz.yalla.client.service.map.response

import kotlinx.serialization.Serializable

@Serializable
data class GetRoutingResponse(
    val distance: Double?,
    val duration: Double?,
    val routing: List<Routing>?
) {
    @Serializable
    data class Routing(
        val lat: Double?,
        val lng: Double?
    )
}