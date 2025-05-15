package uz.yalla.client.feature.order.presentation.driver_waiting.model

import androidx.compose.ui.unit.Dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import uz.yalla.client.core.domain.model.OrderStatus
import uz.yalla.client.feature.order.domain.usecase.order.CancelRideUseCase
import uz.yalla.client.feature.order.domain.usecase.order.GetShowOrderUseCase
import uz.yalla.client.feature.order.presentation.driver_waiting.view.DriverWaitingIntent
import uz.yalla.client.feature.order.presentation.driver_waiting.view.DriverWaitingSheet.mutableIntentFlow
import kotlin.time.Duration.Companion.seconds

class DriverWaitingViewModel(
    private val cancelRideUseCase: CancelRideUseCase,
    private val getShowOrderUseCase: GetShowOrderUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(DriverWaitingState())
    val uiState = _uiState.asStateFlow()

    fun setOrderId(orderId: Int) {
        _uiState.update { it.copy(orderId = orderId) }
        getOrderDetails()
    }

    fun onIntent(intent: DriverWaitingIntent) {
        viewModelScope.launch {
            when (intent) {
                is DriverWaitingIntent.SetFooterHeight -> setFooterHeight(intent.height)
                is DriverWaitingIntent.SetHeaderHeight -> setHeaderHeight(intent.height)
                else -> mutableIntentFlow.emit(intent)
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
                cancelRideUseCase(orderId)
            }
        }.invokeOnCompletion {
            onIntent(DriverWaitingIntent.OnCancelled(uiState.value.orderId))
        }
    }

    fun infiniteTimer(isActive: Boolean) = flow {
        var seconds = 0
        while (isActive && currentCoroutineContext().isActive) {
            delay(1.seconds)
            emit(seconds)
            seconds++
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