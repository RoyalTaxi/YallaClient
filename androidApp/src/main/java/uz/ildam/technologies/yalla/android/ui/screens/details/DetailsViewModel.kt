package uz.ildam.technologies.yalla.android.ui.screens.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import uz.ildam.technologies.yalla.core.domain.error.Result
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
        when (val result = getOrderHistoryUseCase(orderId = orderId)) {
            is Result.Error -> _actionState.emit(DetailsActionState.Error(result.error))

            is Result.Success -> {
                _actionState.emit(DetailsActionState.DetailsSuccess)
                _uiState.update { it.copy(orderDetails = result.data) }
            }
        }
    }

    fun getMapPoints() = viewModelScope.launch {
        _actionState.emit(DetailsActionState.Loading)
        when (
            val result = getTariffsUseCase(
                coords = _uiState.value.orderDetails?.taxi?.routes?.map { Pair(it.cords.lat, it.cords.lng) }.orEmpty(),
                optionIds = emptyList(),
                addressId = 221
            )
        ) {
            is Result.Error -> _actionState.emit(DetailsActionState.Error(result.error))
            is Result.Success -> {
                _actionState.emit(DetailsActionState.RouteSuccess)
                _uiState.update { it.copy(routes = result.data.map.routing) }
            }
        }
    }
}