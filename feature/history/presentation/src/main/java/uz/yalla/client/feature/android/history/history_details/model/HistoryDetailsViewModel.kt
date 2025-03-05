package uz.yalla.client.feature.android.history.history_details.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import uz.yalla.client.feature.domain.usecase.GetOrderHistoryUseCase
import uz.yalla.client.feature.order.domain.usecase.tariff.GetTariffsUseCase

internal class HistoryDetailsViewModel(
    private val getOrderHistoryUseCase: GetOrderHistoryUseCase,
    private val getTariffsUseCase: GetTariffsUseCase
) : ViewModel() {
    private val _actionState = MutableSharedFlow<HistoryDetailsActionState>()
    val actionState = _actionState.asSharedFlow()

    private val _uiState = MutableStateFlow(HistoryDetailsUIState())
    val uiState = _uiState.asStateFlow()

    fun getOrderHistory(orderId: Int) = viewModelScope.launch {
        _actionState.emit(HistoryDetailsActionState.Loading)
        getOrderHistoryUseCase(orderId)
            .onSuccess { result ->
                _actionState.emit(HistoryDetailsActionState.DetailsSuccess)
                _uiState.update { it.copy(orderDetails = result) }
            }
            .onFailure {
                _actionState.emit(HistoryDetailsActionState.Error)
            }
    }

    fun getMapPoints() = viewModelScope.launch {
        _actionState.emit(HistoryDetailsActionState.Loading)
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
            _actionState.emit(HistoryDetailsActionState.RouteSuccess)
            _uiState.update { it.copy(routes = result.map.routing) }
        }.onFailure { _actionState.emit(HistoryDetailsActionState.Error) }
    }
}