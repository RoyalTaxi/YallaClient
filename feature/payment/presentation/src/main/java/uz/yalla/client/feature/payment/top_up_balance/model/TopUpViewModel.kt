package uz.yalla.client.feature.payment.top_up_balance.model

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

internal class TopUpViewModel(

) : ViewModel() {

    private var _uiState = MutableStateFlow(TopUpUIState())
    val uiState = _uiState.asStateFlow()

    fun updateBalance(topUpAmount: String) {
        _uiState.update { currentState ->
            currentState.copy(topUpAmount = topUpAmount)
        }
    }
}