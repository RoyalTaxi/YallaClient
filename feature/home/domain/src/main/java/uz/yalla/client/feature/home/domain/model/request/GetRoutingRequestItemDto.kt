package uz.yalla.client.feature.home.domain.model.request

data class GetRoutingRequestItemDto(
    val type: String,
    val lng: Double,
    val lat: Double
)