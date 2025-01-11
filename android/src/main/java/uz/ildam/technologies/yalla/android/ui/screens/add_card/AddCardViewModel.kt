package uz.ildam.technologies.yalla.android.ui.screens.add_card

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import uz.ildam.technologies.yalla.feature.payment.domain.usecase.AddCardUseCase

class AddCardViewModel(
    private val addCardUseCase: AddCardUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddCardUIState())
    val uiState = _uiState.asStateFlow()

    private val _actionState = MutableSharedFlow<AddCardActionState>()
    val actionState = _actionState.asSharedFlow()

    fun addCard() = viewModelScope.launch {
        uiState.value.apply {
            _actionState.emit(AddCardActionState.Loading)
            addCardUseCase(number = cardNumber, expiry = cardExpiry)
                .onSuccess { result ->
                    _actionState.emit(
                        AddCardActionState.Success(
                            key = result.key,
                            cardNumber = uiState.value.cardNumber,
                            cardExpiry = uiState.value.cardExpiry
                        )
                    )
                }.onFailure { _actionState.emit(AddCardActionState.Error) }
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