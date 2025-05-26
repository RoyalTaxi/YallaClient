package uz.yalla.client.feature.order.presentation.on_the_ride.model

import androidx.compose.ui.unit.Dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import uz.yalla.client.feature.order.domain.usecase.order.GetShowOrderUseCase
import uz.yalla.client.feature.order.presentation.on_the_ride.view.OnTheRideSheetChannel
import uz.yalla.client.feature.order.presentation.on_the_ride.view.OnTheRideSheetIntent

class OnTheRideSheetViewModel(
    private val getShowOrderUseCase: GetShowOrderUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(OnTheRideSheetState())
    val uiState = _uiState.asStateFlow()

    fun setOrderId(orderId: Int) {
        _uiState.update { it.copy(orderId = orderId) }
        getOrderDetails()
    }

    fun onIntent(intent: OnTheRideSheetIntent) {
        viewModelScope.launch(Dispatchers.IO) {
            when (intent) {
                is OnTheRideSheetIntent.SetHeaderHeight -> setHeaderHeight(intent.height)
                else -> OnTheRideSheetChannel.sendIntent(intent)
            }
        }
    }

    private fun getOrderDetails() {
        val orderId = uiState.value.orderId ?: return
        viewModelScope.launch {
            getShowOrderUseCase(orderId).onSuccess { data ->
                _uiState.update { it.copy(selectedDriver = data) }
            }
        }
    }

    fun setDetailsBottomSheetVisibility(isVisible: Boolean) {
        _uiState.update { it.copy(detailsBottomSheetVisibility = isVisible) }
    }

    private fun setHeaderHeight(height: Dp) {
        _uiState.update { it.copy(headerHeight = height) }
    }
}