package uz.yalla.client.feature.history.history_details.model

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import uz.yalla.client.core.common.viewmodel.BaseViewModel
import uz.yalla.client.feature.domain.usecase.GetOrderHistoryUseCase
import uz.yalla.client.feature.order.domain.usecase.tariff.GetTariffsUseCase

 class HistoryDetailsViewModel(
    private val getOrderHistoryUseCase: GetOrderHistoryUseCase,
    private val getTariffsUseCase: GetTariffsUseCase
) : BaseViewModel() {

    private val _uiState = MutableStateFlow(HistoryDetailsUIState())
    val uiState = _uiState.asStateFlow()

    fun getOrderHistory(orderId: Int) = viewModelScope.launchWithLoading {
        getOrderHistoryUseCase(orderId)
            .onSuccess { result ->
                _uiState.update { it.copy(orderDetails = result) }
                getMapPoints()
            }
            .onFailure(::handleException)
    }

    fun getMapPoints() = viewModelScope.launch {
        getTariffsUseCase(
            coords = _uiState.value.orderDetails?.taxi?.routes?.map {
                Pair(
                    it.cords.lat,
                    it.cords.lng
                )
            }.orEmpty(),
            optionIds = emptyList()
        ).onSuccess { result ->
            _uiState.update { it.copy(routes = result.map.routing) }
        }.onFailure(::handleException)
    }
}