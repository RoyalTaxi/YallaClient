package uz.yalla.client.feature.places.places.model

import androidx.lifecycle.viewModelScope

internal fun AddressesViewModel.findAllAddresses() = intent {
    viewModelScope.launchWithLoading {
        findAllPlacesUseCase()
            .onSuccess { result ->
                intent {
                    reduce {
                        state.copy(addresses = result)
                    }
                }
            }
            .onFailure(::handleException)
    }
}