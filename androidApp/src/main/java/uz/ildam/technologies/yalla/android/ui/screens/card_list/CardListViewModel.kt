package uz.ildam.technologies.yalla.android.ui.screens.card_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import uz.ildam.technologies.yalla.core.data.enums.PaymentType
import uz.ildam.technologies.yalla.core.data.local.AppPreferences
import uz.ildam.technologies.yalla.core.domain.error.Result
import uz.ildam.technologies.yalla.feature.payment.domain.usecase.GetCardListUseCase

class CardListViewModel(
    private val getCardListUseCase: GetCardListUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CardListUIState())
    val uiState = _uiState.asStateFlow()

    private val _actionState = MutableSharedFlow<CardListActionState>()
    val actionState = _actionState.asSharedFlow()

    fun getCardList() = viewModelScope.launch {
        _actionState.emit(CardListActionState.Loading)
        when (val result = getCardListUseCase()) {
            is Result.Error -> _actionState.emit(CardListActionState.Error)
            is Result.Success -> {
                _uiState.update { it.copy(cards = result.data) }
                _actionState.emit(CardListActionState.Success)
            }
        }
    }

    fun selectPaymentType(paymentType: PaymentType) {
        AppPreferences.paymentType = paymentType
        _uiState.update { it.copy(selectedPaymentType = paymentType) }
    }
}