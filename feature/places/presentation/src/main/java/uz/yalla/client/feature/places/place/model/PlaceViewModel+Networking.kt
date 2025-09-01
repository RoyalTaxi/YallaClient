package uz.yalla.client.feature.places.place.model

import androidx.lifecycle.viewModelScope
import uz.yalla.client.feature.places.place.intent.PlaceSideEffect

internal fun PlaceViewModel.findOneAddress(id: Int) = intent {
    viewModelScope.launchWithLoading {
        findOnePlaceUseCase(id)
            .onSuccess { result ->
                intent {
                    reduce {
                        state.copy(place = result)
                    }
                }
            }
            .onFailure(::handleException)
    }
}

internal fun PlaceViewModel.deleteOneAddress(id: Int) = intent {
    viewModelScope.launchWithLoading {
        deleteOnePlaceUseCase(id)
            .onSuccess { postSideEffect(PlaceSideEffect.NavigateBack) }
            .onFailure(::handleException)
    }
}

internal fun PlaceViewModel.updateOneAddress(id: Int) = intent {
    viewModelScope.launchWithLoading {
        state.place.let { place ->
            if (place.coords.takeIf { it.lat != 0.0 && it.lng != 0.0 } != null)
                updateOnePlaceUseCase(id = id, body = place.mapToPlaceDto())
                    .onSuccess { postSideEffect(PlaceSideEffect.NavigateBack) }
                    .onFailure(::handleException)
        }
    }
}

internal fun PlaceViewModel.createOneAddress() = intent {
    viewModelScope.launchWithLoading {
        state.place.let { place ->
            if (
                place.coords.takeIf { it.lat != 0.0 && it.lng != 0.0 } != null
            ) postOnePlaceUseCase(place.mapToPlaceDto())
                .onSuccess { postSideEffect(PlaceSideEffect.NavigateBack) }
                .onFailure(::handleException)
        }
    }
}

