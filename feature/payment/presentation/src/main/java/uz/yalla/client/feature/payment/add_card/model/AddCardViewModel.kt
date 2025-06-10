package uz.yalla.client.feature.payment.add_card.model

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import uz.yalla.client.core.common.viewmodel.BaseViewModel
import uz.yalla.client.feature.payment.domain.usecase.AddCardUseCase

internal class AddCardViewModel(
    private val addCardUseCase: AddCardUseCase,
) : BaseViewModel() {

    private val _uiState = MutableStateFlow(AddCardUIState())
    val uiState = _uiState.asStateFlow()

    private val _navigationChannel: Channel<String> = Channel(Channel.CONFLATED)
    val navigationChannel = _navigationChannel.receiveAsFlow()

    fun addCard() = viewModelScope.launchWithLoading {
        uiState.value.apply {
            addCardUseCase(number = cardNumber, expiry = cardExpiry)
                .onSuccess { result ->
                    _navigationChannel.send(result.key)
                }.onFailure(::handleException)
        }
    }

    fun setCardNumber(cardNumber: String) {
        if (cardNumber.length <= 16 && cardNumber.all { it.isDigit() })
            _uiState.update { it.copy(cardNumber = cardNumber) }

        uiState.value.apply {
            _uiState.update { it.copy(buttonState = isNumberValid() && isExpiryValid()) }
        }
    }

    fun setCardDate(cardDate: String) {
        if (
            cardDate.length <= 4 &&
            cardDate.all { it.isDigit() }
        ) _uiState.update { it.copy(cardExpiry = cardDate) }

        uiState.value.apply {
            _uiState.update { it.copy(buttonState = isNumberValid() && isExpiryValid()) }
        }
    }
}