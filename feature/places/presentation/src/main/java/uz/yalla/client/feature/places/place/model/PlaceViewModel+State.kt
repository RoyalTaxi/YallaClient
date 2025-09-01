package uz.yalla.client.feature.places.place.model

import uz.yalla.client.core.domain.model.type.PlaceType

internal fun PlaceViewModel.updateName(name: String) = intent {
    reduce {
        state.copy(place = state.place.copy(name = name))
    }
}

internal fun PlaceViewModel.updateType(type: PlaceType) = intent {
    reduce {
        state.copy(place = state.place.copy(type = type))
    }
}

internal fun PlaceViewModel.updateSelectedAddress(
    address: String,
    lat: Double,
    lng: Double
) = intent {
    reduce {
        state.copy(
            place = state.place.coords.let {
                state.place.copy(
                    address = address,
                    coords = it.copy(lat = lat, lng = lng)
                )
            }
        )
    }
}

internal fun PlaceViewModel.updateApartment(apartment: String) = intent {
    reduce {
        state.copy(place = state.place.copy(apartment = apartment))
    }
}

internal fun PlaceViewModel.updateEnter(enter: String) = intent {
    reduce {
        state.copy(place = state.place.copy(enter = enter))
    }
}

internal fun PlaceViewModel.updateFloor(floor: String) = intent {
    reduce {
        state.copy(place = state.place.copy(floor = floor))
    }
}

internal fun PlaceViewModel.updateComment(comment: String) = intent {
    reduce {
        state.copy(place = state.place.copy(comment = comment))
    }
}

internal fun PlaceViewModel.setSearchVisibility(visible: Boolean) = intent {
    reduce {
        state.copy(isSearchVisible = visible)
    }
}

internal fun PlaceViewModel.setMapVisibility(visible: Boolean) = intent {
    reduce {
        state.copy(isMapVisible = visible)
    }
}

internal fun PlaceViewModel.setConfirmationVisibility(visible: Boolean) = intent {
    reduce {
        state.copy(isConfirmationVisible = visible)
    }
}

internal fun PlaceViewModel.setDeleteId(deleteId: Int) = intent {
    reduce {
        state.copy(deleteId = deleteId)
    }
}
