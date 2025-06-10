package uz.yalla.client.feature.places.place.model

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import uz.yalla.client.core.common.viewmodel.BaseViewModel
import uz.yalla.client.core.domain.model.type.PlaceType
import uz.yalla.client.feature.order.domain.model.response.PlaceModel
import uz.yalla.client.feature.order.domain.usecase.DeleteOnePlaceUseCase
import uz.yalla.client.feature.order.domain.usecase.FindOnePlaceUseCase
import uz.yalla.client.feature.order.domain.usecase.PostOnePlaceUseCase
import uz.yalla.client.feature.order.domain.usecase.UpdateOnePlaceUseCase

internal class PlaceViewModel(
    private val findOnePlaceUseCase: FindOnePlaceUseCase,
    private val postOnePlaceUseCase: PostOnePlaceUseCase,
    private val updateOnePlaceUseCase: UpdateOnePlaceUseCase,
    private val deleteOnePlaceUseCase: DeleteOnePlaceUseCase
) : BaseViewModel() {

    private val _place = MutableStateFlow(PlaceModel.EMPTY)
    val place = _place.asStateFlow()

    private val _navigationChannel: Channel<Unit> = Channel(Channel.CONFLATED)
    val navigationChannel = _navigationChannel.receiveAsFlow()

    val saveButtonState = place
        .distinctUntilChangedBy { Pair(it.name, it.coords) }
        .map { it.name.isNotBlank() && it.coords.lat != 0.0 && it.coords.lng != 0.0 }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = false
        )

    fun findOneAddress(id: Int) = viewModelScope.launchWithLoading {
        findOnePlaceUseCase(id)
            .onSuccess { result ->
                _place.emit(result)
            }
            .onFailure(::handleException)
    }

    fun deleteOneAddress(id: Int) = viewModelScope.launchWithLoading {
        deleteOnePlaceUseCase(id)
            .onSuccess { _navigationChannel.send(Unit) }
            .onFailure(::handleException)
    }

    fun updateOneAddress(id: Int) = viewModelScope.launchWithLoading {
        place.value.let { place ->
            if (
                place.coords.takeIf { it.lat != 0.0 && it.lng != 0.0 } != null
            ) updateOnePlaceUseCase(
                id = id,
                body = place.mapToPlaceDto()
            )
                .onSuccess { _navigationChannel.send(Unit) }
                .onFailure(::handleException)
        }
    }

    fun createOneAddress() = viewModelScope.launchWithLoading {
        place.value.let { place ->
            if (
                place.coords.takeIf { it.lat != 0.0 && it.lng != 0.0 } != null
            ) postOnePlaceUseCase(place.mapToPlaceDto())
                .onSuccess { _navigationChannel.send(Unit) }
                .onFailure(::handleException)
        }
    }

    fun updateName(name: String) = _place.update {
        it.copy(name = name)
    }

    fun updateType(type: PlaceType) = _place.update {
        it.copy(type = type)
    }

    fun updateSelectedAddress(
        address: String,
        lat: Double,
        lng: Double
    ) = _place.update {
        it.copy(
            address = address,
            coords = it.coords.copy(lat, lng)
        )
    }

    fun updateApartment(apartment: String) = _place.update {
        it.copy(apartment = apartment)
    }

    fun updateEnter(enter: String) = _place.update {
        it.copy(enter = enter)
    }

    fun updateFloor(floor: String) = _place.update {
        it.copy(floor = floor)
    }

    fun updateComment(comment: String) = _place.update {
        it.copy(comment = comment)
    }
}