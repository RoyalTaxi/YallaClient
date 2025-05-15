package uz.yalla.client.feature.payment.card_list.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import uz.yalla.client.core.domain.local.AppPreferences
import uz.yalla.client.core.domain.model.PaymentType
import uz.yalla.client.feature.payment.domain.usecase.GetCardListUseCase

internal class CardListViewModel(
    private val getCardListUseCase: GetCardListUseCase,
    private val prefs: AppPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(CardListUIState())
    val uiState = _uiState.asStateFlow()

    private val _actionState = MutableSharedFlow<CardListActionState>()
    val actionState = _actionState.asSharedFlow()

    init {
        viewModelScope.launch {
            prefs.paymentType.collectLatest { type ->
                _uiState.update { it.copy(selectedPaymentType = type) }
            }
        }
    }

    fun getCardList() = viewModelScope.launch {
        _actionState.emit(CardListActionState.Loading)
        getCardListUseCase().onSuccess { result ->
            _uiState.update { it.copy(cards = result) }
            _actionState.emit(CardListActionState.Success)
        }.onFailure {
            _actionState.emit(CardListActionState.Error)
        }
    }

    fun selectPaymentType(paymentType: PaymentType) {
        viewModelScope.launch {
            prefs.setPaymentType(paymentType)
        }
    }
}