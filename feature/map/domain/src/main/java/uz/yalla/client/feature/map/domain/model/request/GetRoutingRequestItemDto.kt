package uz.yalla.client.feature.map.domain.model.request

data class GetRoutingRequestItemDto(
    val type: String,
    val lng: Double,
    val lat: Double
)