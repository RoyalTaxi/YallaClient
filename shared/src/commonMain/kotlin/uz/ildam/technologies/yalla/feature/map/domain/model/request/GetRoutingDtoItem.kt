package uz.ildam.technologies.yalla.feature.map.domain.model.request

data class GetRoutingDtoItem(
    val type: String,
    val lng: Double,
    val lat: Double
)