package uz.yalla.client.feature.order.presentation.driver_waiting.model

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
        viewModelScope.launch(Dispatchers.IO) {
            mutableIntentFlow.emit(intent)
        }
    }

    private fun getOrderDetails() {
        val orderId = uiState.value.orderId ?: return
        viewModelScope.launch(Dispatchers.IO) {
            getShowOrderUseCase(orderId).onSuccess { data ->
                _uiState.update { it.copy(selectedDriver = data) }
            }
        }
    }

    fun cancelRide() {
        val orderId = uiState.value.orderId
        viewModelScope.launch(Dispatchers.IO) {
            if (orderId != null) {
                cancelRideUseCase(orderId)
            }
        }.invokeOnCompletion {
            onIntent(DriverWaitingIntent.OnCancelled(uiState.value.orderId))
        }
    }

    fun timer(range: IntRange) = flow {
        for (second in range) {
            delay(1.seconds)
            emit(second)
            if (!currentCoroutineContext().isActive) return@flow
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
        _uiState.update { it.copy(cancelBottomSheetVisibility = isVisible) }
    }
}