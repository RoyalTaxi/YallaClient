package uz.yalla.client.feature.places.places.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import uz.yalla.client.feature.order.domain.usecase.FindAllPlacesUseCase

internal class AddressesViewModel(
    private val findAllPlacesUseCase: FindAllPlacesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddressesUIState())
    val uiState = _uiState.asStateFlow()

    private val _actionState = MutableSharedFlow<AddressesActionState>()
    val actionState = _actionState.asSharedFlow()

    fun findAllAddresses() = viewModelScope.launch(Dispatchers.IO) {
        _actionState.emit(AddressesActionState.Loading)
        findAllPlacesUseCase()
            .onSuccess { result ->
                _uiState.update { it.copy(addresses = result) }
                _actionState.emit(AddressesActionState.Success)
            }
            .onFailure { _actionState.emit(AddressesActionState.Error) }
    }
}