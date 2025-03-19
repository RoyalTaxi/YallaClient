package uz.yalla.client.feature.places.place.model

import uz.yalla.client.feature.order.domain.model.type.PlaceType

internal data class PlaceUIState(
    val placeType: PlaceType = PlaceType.OTHER,
    val selectedAddress: Location? = null,
    val addressName: String = "",
    val apartment: String = "",
    val entrance: String = "",
    val floor: String = "",
    val comment: String = ""
) {
    data class Location(
        val name: String = "",
        val lat: Double = 0.0,
        val lng: Double = 0.0
    )
}