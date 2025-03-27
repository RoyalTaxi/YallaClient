package uz.yalla.client.feature.order.presentation.on_ride.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import uz.yalla.client.feature.order.presentation.on_ride.view.OnTheRideSheet.mutableIntentFlow
import uz.yalla.client.feature.order.presentation.on_ride.view.OnTheRideSheetIntent

class OnTheRideSheetViewModel() :ViewModel() {

    private val _uiState = MutableStateFlow(OnTheRideSheetState())
    val uiState = _uiState.asStateFlow()

    fun onIntent(intent: OnTheRideSheetIntent) {
        viewModelScope.launch ( Dispatchers.IO ) {
            mutableIntentFlow.emit(intent)
        }
    }
}