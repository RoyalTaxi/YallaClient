package uz.yalla.client.feature.order.domain.model.response

import uz.yalla.client.core.domain.model.type.PlaceType

data class PlaceModel(
    val id: Int,
    val name: String,
    val address: String,
    val coords: Coords,
    val type: PlaceType,
    val enter: String,
    val apartment: String,
    val floor: String,
    val comment: String,
) {
    data class Coords(
        val lat: Double,
        val lng: Double
    )
}