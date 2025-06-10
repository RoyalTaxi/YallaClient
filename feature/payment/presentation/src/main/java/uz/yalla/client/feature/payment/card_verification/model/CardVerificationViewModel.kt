package uz.yalla.client.feature.payment.card_verification.model

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import uz.yalla.client.core.common.viewmodel.BaseViewModel
import uz.yalla.client.feature.payment.domain.usecase.AddCardUseCase
import uz.yalla.client.feature.payment.domain.usecase.VerifyCardUseCase
import kotlin.time.Duration.Companion.seconds

internal class CardVerificationViewModel(
    private val addCardUseCase: AddCardUseCase,
    private val verifyCardUseCase: VerifyCardUseCase
) : BaseViewModel() {

    private val _uiState = MutableStateFlow(CardVerificationUIState())
    val uiState = _uiState.asStateFlow()

    private val _navigationChannel: Channel<Unit> = Channel(Channel.CONFLATED)
    val navigationChannel = _navigationChannel.receiveAsFlow()

    fun countDownTimer(countDown: Int) = flow {
        for (seconds in countDown downTo 0) {
            delay(1.seconds)
            emit(seconds)
            if (!currentCoroutineContext().isActive) return@flow
        }
    }

    fun addCard() = viewModelScope.launchWithLoading {
        _uiState.update { it.copy(code = "") }
        uiState.value.apply {
            addCardUseCase(number = cardNumber, expiry = cardExpiry)
                .onSuccess {
                    countDownTimer(60).collectLatest { seconds ->
                        updateUiState(
                            buttonState = seconds != 0 && code.length == 6,
                            remainingMinutes = seconds / 60,
                            remainingSeconds = seconds % 60,
                            hasRemainingTime = seconds > 0
                        )
                    }
                }
                .onFailure(::handleException)
        }

    }

    fun verifyCard() = viewModelScope.launchWithLoading {
        verifyCardUseCase(key = uiState.value.key, confirmCode = uiState.value.code)
            .onSuccess { _navigationChannel.send(Unit) }
            .onFailure(::handleException)
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
