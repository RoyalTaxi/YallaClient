package uz.ildam.technologies.yalla.android.ui.screens.address

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import uz.ildam.technologies.yalla.core.domain.error.Result
import uz.ildam.technologies.yalla.feature.addresses.domain.model.request.PostOneAddressDto
import uz.ildam.technologies.yalla.feature.addresses.domain.model.response.AddressType
import uz.ildam.technologies.yalla.feature.addresses.domain.usecase.DeleteOneAddressUseCase
import uz.ildam.technologies.yalla.feature.addresses.domain.usecase.FindOneAddressUseCase
import uz.ildam.technologies.yalla.feature.addresses.domain.usecase.PostOneAddressUseCase
import uz.ildam.technologies.yalla.feature.addresses.domain.usecase.UpdateOneAddressUseCase

class AddressViewModel(
    private val findOneAddressUseCase: FindOneAddressUseCase,
    private val postOneAddressUseCase: PostOneAddressUseCase,
    private val updateOneAddressUseCase: UpdateOneAddressUseCase,
    private val deleteOneAddressUseCase: DeleteOneAddressUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddressUIState())
    val uiState = _uiState.asStateFlow()

    private val _actionState = MutableSharedFlow<AddressActionState>()
    val actionState = _actionState.asSharedFlow()

    fun findOneAddress(id: Int) = viewModelScope.launch {
        _actionState.emit(AddressActionState.Loading)
        when (val result = findOneAddressUseCase(id)) {
            is Result.Error -> _actionState.emit(AddressActionState.Error)
            is Result.Success -> {
                _uiState.update {
                    it.copy(
                        addressType = result.data.type,
                        addressName = result.data.name,
                        apartment = result.data.apartment,
                        entrance = result.data.enter,
                        floor = result.data.floor,
                        comment = result.data.comment,
                        selectedAddress = AddressUIState.Location(
                            name = result.data.address,
                            lat = result.data.coords.lat,
                            lng = result.data.coords.lng
                        )
                    )
                }
                _actionState.emit(AddressActionState.GetSuccess)
            }
        }
    }

    fun deleteOneAddress(id: Int) = viewModelScope.launch {
        _actionState.emit(AddressActionState.Loading)
        when (deleteOneAddressUseCase(id)) {
            is Result.Error -> _actionState.emit(AddressActionState.Error)
            is Result.Success -> _actionState.emit(AddressActionState.DeleteSuccess)
        }
    }

    fun updateOneAddress(id: Int) = viewModelScope.launch {
        _actionState.emit(AddressActionState.Loading)
        uiState.value.let { state ->
            if (state.selectedAddress != null) when (
                updateOneAddressUseCase(
                    id = id,
                    body = PostOneAddressDto(
                        name = state.addressName,
                        address = state.selectedAddress.name,
                        lat = state.selectedAddress.lat,
                        lng = state.selectedAddress.lng,
                        type = state.addressType.typeName,
                        enter = state.entrance,
                        apartment = state.apartment,
                        floor = state.floor,
                        comment = state.comment
                    )
                )) {
                is Result.Error -> _actionState.emit(AddressActionState.Error)
                is Result.Success -> _actionState.emit(AddressActionState.PutSuccess)
            }
        }
    }

    fun createOneAddress() = viewModelScope.launch {
        _actionState.emit(AddressActionState.Loading)
        uiState.value.let { state ->
            if (state.selectedAddress != null) when (
                postOneAddressUseCase(
                    body = PostOneAddressDto(
                        name = state.addressName,
                        address = state.selectedAddress.name,
                        lat = state.selectedAddress.lat,
                        lng = state.selectedAddress.lng,
                        type = state.addressType.typeName,
                        enter = state.entrance,
                        apartment = state.apartment,
                        floor = state.floor,
                        comment = state.comment
                    )
                )) {
                is Result.Error -> _actionState.emit(AddressActionState.Error)
                is Result.Success -> _actionState.emit(AddressActionState.PutSuccess)
            }
        }
    }

    fun updateName(name: String) = _uiState.update {
        it.copy(addressName = name)
    }

    fun updateType(type: AddressType) = _uiState.update {
        it.copy(addressType = type)
    }

    fun updateSelectedAddress(address: AddressUIState.Location) = _uiState.update {
        it.copy(selectedAddress = address)
    }

    fun updateApartment(apartment: String) = _uiState.update {
        it.copy(apartment = apartment)
    }

    fun updateEnter(enter: String) = _uiState.update {
        it.copy(entrance = enter)
    }

    fun updateFloor(floor: String) = _uiState.update {
        it.copy(floor = floor)
    }

    fun updateComment(comment: String) = _uiState.update {
        it.copy(comment = comment)
    }
}