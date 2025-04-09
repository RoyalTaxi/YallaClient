package uz.yalla.client.feature.order.domain.model.response

import uz.yalla.client.core.domain.model.type.PlaceType
import uz.yalla.client.feature.order.domain.model.request.PostOnePlaceDto

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

    fun mapToPlaceDto() = PostOnePlaceDto(
        name = name,
        address = address,
        lat = coords.lat,
        lng = coords.lng,
        type = type.typeName,
        enter = enter,
        apartment = apartment,
        floor = floor,
        comment = comment
    )

    companion object {
        val EMPTY = PlaceModel(
            id = -1,
            name = "",
            address = "",
            coords = Coords(0.0, 0.0),
            type = PlaceType.OTHER,
            enter = "",
            apartment = "",
            floor = "",
            comment = ""
        )
    }
}