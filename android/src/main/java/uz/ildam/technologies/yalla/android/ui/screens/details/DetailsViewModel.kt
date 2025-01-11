package uz.ildam.technologies.yalla.android.ui.screens.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import uz.ildam.technologies.yalla.feature.history.domain.usecase.GetOrderHistoryUseCase
import uz.ildam.technologies.yalla.feature.order.domain.usecase.tariff.GetTariffsUseCase

class DetailsViewModel(
    private val getOrderHistoryUseCase: GetOrderHistoryUseCase,
    private val getTariffsUseCase: GetTariffsUseCase
) : ViewModel() {
    private val _actionState = MutableSharedFlow<DetailsActionState>()
    val actionState = _actionState.asSharedFlow()

    private val _uiState = MutableStateFlow(DetailsUIState())
    val uiState = _uiState.asStateFlow()

    fun getOrderHistory(orderId: Int) = viewModelScope.launch {
        _actionState.emit(DetailsActionState.Loading)
        getOrderHistoryUseCase(orderId)
            .onSuccess { result ->
                _actionState.emit(DetailsActionState.DetailsSuccess)
                _uiState.update { it.copy(orderDetails = result) }
            }
            .onFailure {
                _actionState.emit(DetailsActionState.Error)
            }
    }

    fun getMapPoints() = viewModelScope.launch {
        _actionState.emit(DetailsActionState.Loading)
        getTariffsUseCase(
            coords = _uiState.value.orderDetails?.taxi?.routes?.map {
                Pair(
                    it.cords.lat,
                    it.cords.lng
                )
            }.orEmpty(),
            optionIds = emptyList(),
            addressId = 221
        ).onSuccess { result ->
            _actionState.emit(DetailsActionState.RouteSuccess)
            _uiState.update { it.copy(routes = result.map.routing) }
        }.onFailure { _actionState.emit(DetailsActionState.Error) }
    }
}