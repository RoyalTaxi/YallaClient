package uz.yalla.client.feature.places.places.model

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import uz.yalla.client.core.common.viewmodel.BaseViewModel
import uz.yalla.client.feature.order.domain.usecase.FindAllPlacesUseCase

internal class AddressesViewModel(
    private val findAllPlacesUseCase: FindAllPlacesUseCase
) : BaseViewModel() {

    private val _uiState = MutableStateFlow(AddressesUIState())
    val uiState = _uiState.asStateFlow()

    fun findAllAddresses() = viewModelScope.launchWithLoading {
        findAllPlacesUseCase()
            .onSuccess { result ->
                _uiState.update { it.copy(addresses = result) }
            }
            .onFailure(::handleException)
    }
}