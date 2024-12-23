package uz.ildam.technologies.yalla.android.ui.screens.addresses

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import uz.ildam.technologies.yalla.core.domain.error.Result
import uz.ildam.technologies.yalla.feature.addresses.domain.usecase.FindAllAddressesUseCase

class AddressesViewModel(
    private val findAllAddressesUseCase: FindAllAddressesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddressesUIState())
    val uiState = _uiState.asStateFlow()

    private val _actionState = MutableSharedFlow<AddressesActionState>()
    val actionState = _actionState.asSharedFlow()

    fun findAllAddresses() = viewModelScope.launch {
        _actionState.emit(AddressesActionState.Loading)
        when (val result = findAllAddressesUseCase()) {
            is Result.Error -> _actionState.emit(AddressesActionState.Error)
            is Result.Success -> {
                _uiState.update { it.copy(addresses = result.data) }
                _actionState.emit(AddressesActionState.Success)
            }
        }
    }
}