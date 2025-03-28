package uz.yalla.client.feature.order.presentation.feedback.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import uz.yalla.client.core.data.mapper.or0
import uz.yalla.client.feature.order.domain.usecase.order.GetShowOrderUseCase
import uz.yalla.client.feature.order.domain.usecase.order.RateTheRideUseCase
import uz.yalla.client.feature.order.presentation.feedback.view.FeedbackSheetIntent
import uz.yalla.client.feature.order.presentation.feedback.view.FeedbackSheet.mutableIntentFlow

class FeedbackSheetViewModel(
    private val getShowOrderUseCase: GetShowOrderUseCase,
    private val rateTheRideUseCase: RateTheRideUseCase
    ) :ViewModel() {

    private val _uiState = MutableStateFlow(FeedbackSheetState())
    val uiState = _uiState.asStateFlow()

    fun onIntent(intent: FeedbackSheetIntent) {
        viewModelScope.launch(Dispatchers.IO) {
            mutableIntentFlow.emit(intent)
        }
    }

    fun setOrderId(orderId: Int) {
        _uiState.update { it.copy(orderId = orderId) }
        getOrderDetails()
    }

    private fun getOrderDetails() {
        val orderId = uiState.value.orderId ?: return
        viewModelScope.launch(Dispatchers.IO) {
            getShowOrderUseCase(orderId).onSuccess { data ->
                _uiState.update { it.copy(order = data) }
            }
        }
    }

    fun rateTheRide(ball: Int) = viewModelScope.launch {
        rateTheRideUseCase(
            ball = ball,
            orderId = uiState.value.orderId.or0(),
            comment = ""
        )
    }
}