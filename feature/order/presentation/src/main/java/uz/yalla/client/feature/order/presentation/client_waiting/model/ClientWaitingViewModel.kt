package uz.yalla.client.feature.order.presentation.client_waiting.model

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import uz.yalla.client.feature.order.domain.model.response.order.ShowOrderModel

class ClientWaitingViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ClientWaitingState())
    val uiState = _uiState.asStateFlow()

    fun setSelectedDriver(driver: ShowOrderModel) {
        _uiState.update { it.copy(selectedDriver = driver) }
    }
}