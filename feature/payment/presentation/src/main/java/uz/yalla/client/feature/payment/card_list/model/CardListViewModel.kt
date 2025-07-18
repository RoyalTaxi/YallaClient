package uz.yalla.client.feature.payment.card_list.model

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import uz.yalla.client.core.common.viewmodel.BaseViewModel
import uz.yalla.client.core.domain.local.AppPreferences
import uz.yalla.client.core.domain.model.PaymentType
import uz.yalla.client.feature.payment.domain.usecase.GetCardListUseCase

internal class CardListViewModel(
    private val getCardListUseCase: GetCardListUseCase,
    private val prefs: AppPreferences
) : BaseViewModel() {

    private val _uiState = MutableStateFlow(CardListUIState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            prefs.paymentType.collectLatest { type ->
                _uiState.update { it.copy(selectedPaymentType = type) }
            }
        }
    }

    fun getCardList() = viewModelScope.launchWithLoading {
        getCardListUseCase().onSuccess { result ->
            _uiState.update { it.copy(cards = result) }
        }.onFailure(::handleException)
    }

    fun selectPaymentType(paymentType: PaymentType) {
        viewModelScope.launch {
            prefs.setPaymentType(paymentType)
        }
    }
}