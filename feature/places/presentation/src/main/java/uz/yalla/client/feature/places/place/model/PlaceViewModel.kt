package uz.yalla.client.feature.places.place.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import uz.yalla.client.feature.order.domain.model.request.PostOnePlaceDto
import uz.yalla.client.feature.order.domain.model.type.PlaceType
import uz.yalla.client.feature.order.domain.usecase.DeleteOnePlaceUseCase
import uz.yalla.client.feature.order.domain.usecase.FindOnePlaceUseCase
import uz.yalla.client.feature.order.domain.usecase.PostOnePlaceUseCase
import uz.yalla.client.feature.order.domain.usecase.UpdateOnePlaceUseCase

internal class PlaceViewModel(
    private val findOnePlaceUseCase: FindOnePlaceUseCase,
    private val postOnePlaceUseCase: PostOnePlaceUseCase,
    private val updateOnePlaceUseCase: UpdateOnePlaceUseCase,
    private val deleteOnePlaceUseCase: DeleteOnePlaceUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(PlaceUIState())
    val uiState = _uiState.asStateFlow()

    private val _actionState = MutableSharedFlow<PlaceActionState>()
    val actionState = _actionState.asSharedFlow()

    fun findOneAddress(id: Int) = viewModelScope.launch {
        _actionState.emit(PlaceActionState.Loading)
        findOnePlaceUseCase(id)
            .onSuccess { result ->
                _uiState.update {
                    it.copy(
                        placeType = result.type,
                        addressName = result.name,
                        apartment = result.apartment,
                        entrance = result.enter,
                        floor = result.floor,
                        comment = result.comment,
                        selectedAddress = PlaceUIState.Location(
                            name = result.address,
                            lat = result.coords.lat,
                            lng = result.coords.lng
                        )
                    )
                }
                _actionState.emit(PlaceActionState.GetSuccess)
            }
            .onFailure { _actionState.emit(PlaceActionState.Error(it.message.orEmpty())) }
    }

    fun deleteOneAddress(id: Int) = viewModelScope.launch {
        _actionState.emit(PlaceActionState.Loading)
        deleteOnePlaceUseCase(id)
            .onSuccess { _actionState.emit(PlaceActionState.DeleteSuccess) }
            .onFailure { _actionState.emit(PlaceActionState.Error(it.message.orEmpty())) }
    }

    fun updateOneAddress(id: Int) = viewModelScope.launch {
        _actionState.emit(PlaceActionState.Loading)
        uiState.value.let { state ->
            if (state.selectedAddress != null) updateOnePlaceUseCase(
                id = id,
                body = PostOnePlaceDto(
                    name = state.addressName,
                    address = state.selectedAddress.name,
                    lat = state.selectedAddress.lat,
                    lng = state.selectedAddress.lng,
                    type = state.placeType.typeName,
                    enter = state.entrance,
                    apartment = state.apartment,
                    floor = state.floor,
                    comment = state.comment
                )
            ).onSuccess { _actionState.emit(PlaceActionState.PutSuccess) }
                .onFailure { _actionState.emit(PlaceActionState.Error(it.message.orEmpty())) }
        }
    }

    fun createOneAddress() = viewModelScope.launch {
        _actionState.emit(PlaceActionState.Loading)
        uiState.value.let { state ->
            if (state.selectedAddress != null) postOnePlaceUseCase(
                body = PostOnePlaceDto(
                    name = state.addressName,
                    address = state.selectedAddress.name,
                    lat = state.selectedAddress.lat,
                    lng = state.selectedAddress.lng,
                    type = state.placeType.typeName,
                    enter = state.entrance,
                    apartment = state.apartment,
                    floor = state.floor,
                    comment = state.comment
                )
            ).onSuccess { _actionState.emit(PlaceActionState.PutSuccess) }
                .onFailure { _actionState.emit(PlaceActionState.Error(it.message.orEmpty())) }
        }
    }

    fun updateName(name: String) = _uiState.update {
        it.copy(addressName = name)
    }

    fun updateType(type: PlaceType) = _uiState.update {
        it.copy(placeType = type)
    }

    fun updateSelectedAddress(address: PlaceUIState.Location) = _uiState.update {
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