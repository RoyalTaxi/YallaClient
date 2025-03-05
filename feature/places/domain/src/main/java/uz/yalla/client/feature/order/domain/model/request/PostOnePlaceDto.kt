package uz.yalla.client.feature.order.domain.model.request

data class PostOnePlaceDto(
    val name: String,
    val address: String,
    val lat: Double,
    val lng: Double,
    val type: String,
    val enter: String,
    val apartment: String,
    val floor: String,
    val comment: String
)