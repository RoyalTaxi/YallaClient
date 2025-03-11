package uz.yalla.client.feature.order.domain.model.response

data class PlaceNameModel(
    val db: Boolean?,
    val displayName: String?,
    val id: Int?,
    val lat: Double,
    val lng: Double
)