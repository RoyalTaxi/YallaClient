package uz.yalla.client.feature.android.payment.card_list.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import uz.yalla.client.core.data.enums.PaymentType
import uz.yalla.client.core.data.local.AppPreferences
import uz.yalla.client.feature.payment.domain.usecase.GetCardListUseCase

internal class CardListViewModel(
    private val getCardListUseCase: GetCardListUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CardListUIState())
    val uiState = _uiState.asStateFlow()

    private val _actionState = MutableSharedFlow<CardListActionState>()
    val actionState = _actionState.asSharedFlow()

    fun getCardList() = viewModelScope.launch {
        _actionState.emit(CardListActionState.Loading)
        getCardListUseCase().onSuccess { result ->
            _uiState.update { it.copy(cards = result) }
            _actionState.emit(CardListActionState.Success)
        }.onFailure { _actionState.emit(CardListActionState.Error) }
    }

    fun selectPaymentType(paymentType: PaymentType) {
        AppPreferences.paymentType = paymentType
        _uiState.update { it.copy(selectedPaymentType = paymentType) }
    }
}