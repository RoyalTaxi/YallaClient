package uz.yalla.client.feature.order.presentation.driver_waiting.model

import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_DIAL
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import uz.yalla.client.feature.order.domain.model.response.order.ShowOrderModel
import uz.yalla.client.feature.order.presentation.driver_waiting.view.DriverWaitingIntent
import uz.yalla.client.feature.order.presentation.driver_waiting.view.DriverWaitingSheet.mutableIntentFlow

class DriverWaitingViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(DriverWaitingState())
    val uiState = _uiState.asStateFlow()

    fun setSelectedDriver(driver: ShowOrderModel.Executor) {
        _uiState.update { it.copy(selectedDriver = driver) }
    }

    fun onIntent(intent: DriverWaitingIntent) {
        viewModelScope.launch(Dispatchers.IO) {
            mutableIntentFlow.emit(intent)
        }
    }

    fun createDialIntent(context: Context): (String) -> Unit = { number ->
        val intent = Intent(ACTION_DIAL).apply { data = "tel:$number".toUri() }
        if (intent.resolveActivity(context.packageManager) != null) context.startActivity(intent)
    }
}