package uz.ildam.technologies.yalla.android.ui.screens.card_verification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import uz.ildam.technologies.yalla.feature.payment.domain.usecase.AddCardUseCase
import uz.ildam.technologies.yalla.feature.payment.domain.usecase.VerifyCardUseCase
import kotlin.time.Duration.Companion.seconds

class CardVerificationViewModel(
    private val addCardUseCase: AddCardUseCase,
    private val verifyCardUseCase: VerifyCardUseCase
) : ViewModel() {

    private val _actionState = MutableSharedFlow<CardVerificationActionState>()
    val actionState = _actionState.asSharedFlow()

    private val _uiState = MutableStateFlow(CardVerificationUIState())
    val uiState = _uiState.asStateFlow()


    fun countDownTimer(countDown: Int) = flow {
        for (seconds in countDown downTo 0) {
            delay(1.seconds)
            emit(seconds)
            if (!currentCoroutineContext().isActive) return@flow
        }
    }

    fun addCard() = viewModelScope.launch {
        _uiState.update { it.copy(code = "") }
        uiState.value.apply {
            _actionState.emit(CardVerificationActionState.Loading)
            addCardUseCase(number = cardNumber, expiry = cardExpiry)
                .onSuccess { _actionState.emit(CardVerificationActionState.ResendSuccess) }
                .onFailure { _actionState.emit(CardVerificationActionState.Error) }
        }

    }

    fun verifyCard() = viewModelScope.launch {
        _actionState.emit(CardVerificationActionState.Loading)
        verifyCardUseCase(key = uiState.value.key, confirmCode = uiState.value.code)
            .onSuccess { _actionState.emit(CardVerificationActionState.VerificationSuccess) }
            .onFailure { _actionState.emit(CardVerificationActionState.Error) }
    }

    fun updateUiState(
        key: String? = null,
        cardNumber: String? = null,
        cardExpiry: String? = null,
        code: String? = null,
        buttonState: Boolean? = null,
        hasRemainingTime: Boolean? = null,
        remainingMinutes: Int? = null,
        remainingSeconds: Int? = null
    ) {
        _uiState.update { currentState ->
            currentState.copy(
                key = key ?: currentState.key,
                cardNumber = cardNumber ?: currentState.cardNumber,
                cardExpiry = cardExpiry ?: currentState.cardExpiry,
                code = code ?: currentState.code,
                buttonState = buttonState ?: currentState.buttonState,
                hasRemainingTime = hasRemainingTime ?: currentState.hasRemainingTime,
                remainingMinutes = remainingMinutes ?: currentState.remainingMinutes,
                remainingSeconds = remainingSeconds ?: currentState.remainingSeconds
            )
        }
    }
}
