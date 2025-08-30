package uz.yalla.client.feature.places.place.intent

import uz.yalla.client.feature.order.domain.model.response.PlaceModel

data class PlaceState(
    val place: PlaceModel,
    val isSearchVisible: Boolean,
    val isMapVisible: Boolean,
    val isConfirmationVisible: Boolean,
    val deleteId: Int?
) {
    val isSaveButtonEnabled: Boolean
        get() = place.let {
            it.name.isNotBlank() && it.coords.lat != 0.0 && it.coords.lng != 0.0
        }

    companion object {
        val INITIAL = PlaceState(
            place = PlaceModel.EMPTY,
            isSearchVisible = false,
            isMapVisible = false,
            isConfirmationVisible = false,
            deleteId = null
        )
    }
}