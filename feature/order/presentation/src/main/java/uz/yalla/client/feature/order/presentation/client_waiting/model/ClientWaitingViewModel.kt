package uz.yalla.client.feature.order.presentation.client_waiting.model

import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_DIAL
import androidx.core.net.toUri
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
import uz.yalla.client.feature.order.presentation.client_waiting.view.ClientWaitingIntent
import uz.yalla.client.feature.order.presentation.client_waiting.view.ClientWaitingSheet.mutableIntentFlow
import uz.yalla.client.feature.order.presentation.driver_waiting.view.DriverWaitingIntent
import kotlin.time.Duration.Companion.seconds

class ClientWaitingViewModel(
    private val cancelRideUseCase: CancelRideUseCase,
    private val getShowOrderUseCase: GetShowOrderUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ClientWaitingState())
    val uiState = _uiState.asStateFlow()

    fun setOrderId(orderId: Int) {
        _uiState.update { it.copy(orderId = orderId) }
        getOrderDetails()
    }

    fun onIntent(intent: ClientWaitingIntent) {
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
            onIntent(ClientWaitingIntent.OnCancelled(uiState.value.orderId))
        }
    }

    fun setDetailsBottomSheetVisibility(isVisible: Boolean) {
        _uiState.update { it.copy(detailsBottomSheetVisibility = isVisible) }
    }

    fun setCancelBottomSheetVisibility(isVisible: Boolean) {
        _uiState.update { it.copy(cancelBottomSheetVisibility = isVisible) }
    }
}