package uz.yalla.client.feature.order.presentation.driver_waiting.model

import androidx.compose.ui.unit.Dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import uz.yalla.client.core.analytics.event.Event
import uz.yalla.client.core.analytics.event.Logger
import uz.yalla.client.core.domain.model.OrderStatus
import uz.yalla.client.feature.order.domain.usecase.order.CancelRideUseCase
import uz.yalla.client.feature.order.domain.usecase.order.GetShowOrderUseCase
import uz.yalla.client.feature.order.presentation.driver_waiting.view.DriverWaitingSheetChannel
import uz.yalla.client.feature.order.presentation.driver_waiting.view.DriverWaitingSheetIntent
import kotlin.time.Duration.Companion.seconds

class DriverWaitingSheetViewModel(
    private val cancelRideUseCase: CancelRideUseCase,
    private val getShowOrderUseCase: GetShowOrderUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(DriverWaitingSheetState())
    val uiState = _uiState.asStateFlow()

    fun setOrderId(orderId: Int) {
        _uiState.update { it.copy(orderId = orderId) }
        getOrderDetails()
    }

    fun onIntent(intent: DriverWaitingSheetIntent) {
        viewModelScope.launch {
            when (intent) {
                is DriverWaitingSheetIntent.SetFooterHeight -> setFooterHeight(intent.height)
                is DriverWaitingSheetIntent.SetHeaderHeight -> setHeaderHeight(intent.height)
                else -> DriverWaitingSheetChannel.sendIntent(intent)
            }
        }
    }

    private fun getOrderDetails() {
        val orderId = uiState.value.orderId ?: return
        _uiState.update { it.copy(isOrderCancellable = false) }

        viewModelScope.launch {
            getShowOrderUseCase(orderId).onSuccess { data ->
                _uiState.update {
                    it.copy(
                        selectedDriver = data,
                        isOrderCancellable = data.status in OrderStatus.cancellable
                    )
                }
            }
        }
    }

    fun cancelRide() {
        val orderId = uiState.value.orderId
        viewModelScope.launch {
            if (orderId != null) {
                cancelRideUseCase(orderId).onSuccess {
                    Logger.log(Event.OrderCancelled)
                }
            }
        }.invokeOnCompletion {
            onIntent(DriverWaitingSheetIntent.OnCancelled(uiState.value.orderId))
        }
    }

    fun setDetailsBottomSheetVisibility(isVisible: Boolean) {
        _uiState.update { it.copy(detailsBottomSheetVisibility = isVisible) }
    }

    fun setCancelBottomSheetVisibility(isVisible: Boolean) {
        if (isVisible) getOrderDetails()
        _uiState.update { it.copy(cancelBottomSheetVisibility = isVisible) }
    }

    private fun setHeaderHeight(height: Dp) {
        _uiState.update { it.copy(headerHeight = height) }
    }

    private fun setFooterHeight(height: Dp) {
        _uiState.update { it.copy(footerHeight = height) }
    }
}