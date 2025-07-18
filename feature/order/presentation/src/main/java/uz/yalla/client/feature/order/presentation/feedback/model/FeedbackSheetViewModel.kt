package uz.yalla.client.feature.order.presentation.feedback.model

import androidx.compose.ui.unit.Dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import uz.yalla.client.core.data.mapper.or0
import uz.yalla.client.feature.order.domain.usecase.order.GetShowOrderUseCase
import uz.yalla.client.feature.order.domain.usecase.order.RateTheRideUseCase
import uz.yalla.client.feature.order.presentation.feedback.view.FeedbackSheetChannel
import uz.yalla.client.feature.order.presentation.feedback.view.FeedbackSheetIntent

class FeedbackSheetViewModel(
    private val getShowOrderUseCase: GetShowOrderUseCase,
    private val rateTheRideUseCase: RateTheRideUseCase
    ) :ViewModel() {

    private val _uiState = MutableStateFlow(FeedbackSheetState())
    val uiState = _uiState.asStateFlow()

    fun onIntent(intent: FeedbackSheetIntent) {
        viewModelScope.launch {
            when (intent) {
                is FeedbackSheetIntent.SetFooterHeight -> setFooterHeight(intent.height)
                is FeedbackSheetIntent.SetHeaderHeight -> setHeaderHeight(intent.height)
                else -> FeedbackSheetChannel.sendIntent(intent)
            }
        }
    }

    fun setOrderId(orderId: Int) {
        _uiState.update { it.copy(orderId = orderId) }
        getOrderDetails()
    }

    private fun getOrderDetails() {
        val orderId = uiState.value.orderId ?: return
        viewModelScope.launch {
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

    private fun setHeaderHeight(height: Dp) {
        _uiState.update { it.copy(headerHeight = height) }
    }

    private fun setFooterHeight(height: Dp) {
        _uiState.update { it.copy(footerHeight = height) }
    }

}