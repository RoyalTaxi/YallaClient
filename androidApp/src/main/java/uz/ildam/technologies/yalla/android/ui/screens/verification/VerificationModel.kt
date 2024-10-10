package uz.ildam.technologies.yalla.android.ui.screens.verification

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import uz.ildam.technologies.yalla.feature.auth.domain.usecase.auth.SendAuthCodeUseCase
import uz.ildam.technologies.yalla.feature.auth.domain.usecase.auth.ValidateAuthCodeUseCase
import java.util.Locale
import kotlin.time.Duration.Companion.seconds

class VerificationModel(
    private val validateAuthCodeUseCase: ValidateAuthCodeUseCase,
    private val sendAuthCodeUseCase: SendAuthCodeUseCase
) : ScreenModel {

    private val _uiState = MutableStateFlow(ValidationUIState())
    val uiState = _uiState.asStateFlow()

    private val successChannel = Channel<Boolean>(Channel.BUFFERED)
    val successFlow = successChannel.receiveAsFlow()

    private var timerJob: Job? = null

    fun validateAuthCode() = screenModelScope.launch {
        _uiState.update { it.copy(buttonEnabled = false) }
        validateAuthCodeUseCase(
            uiState.value.getFormattedNumber(),
            uiState.value.otp.toInt()
        ).onSuccess { data ->
            _uiState.update { it.copy(secretKey = data.key) }
            successChannel.send(data.isClient)
        }
    }.invokeOnCompletion { _uiState.update { it.copy(buttonEnabled = true) } }

    fun resendAuthCode() = screenModelScope.launch {
        sendAuthCodeUseCase(uiState.value.getFormattedNumber()).onSuccess { data ->
            _uiState.update {
                it.copy(
                    otp = "",
                    timerActiveState = true
                )
            }
            startTimer(data.time)
        }
    }

    fun startTimer(resendTimer: Int) {
        timerJob?.cancel()
        timerJob = screenModelScope.launch {
            timerFlow(resendTimer)
                .onCompletion {
                    _uiState.update {
                        it.copy(timerActiveState = false, buttonEnabled = false)
                    }
                }
                .collect { timeRemaining ->
                    val minutes = timeRemaining / 60
                    val remainingSeconds = timeRemaining % 60
                    _uiState.update {
                        it.copy(
                            resendInSecondsText = String.format(
                                Locale.US,
                                "%d:%02d",
                                minutes,
                                remainingSeconds
                            ),
                            timerActiveState = timeRemaining > 0
                        )
                    }
                }
        }
    }

    private fun timerFlow(resendTimer: Int) = flow {
        var seconds = resendTimer
        while (seconds > 0) {
            emit(seconds)
            delay(1.seconds)
            seconds--
            if (!currentCoroutineContext().isActive) return@flow
        }
        emit(0)
    }

    fun updateOtp(otp: String) {
        if (otp.length <= 5 && otp.all { it.isDigit() })
            _uiState.update { it.copy(otp = otp) }
        if (otp.length == 5 && otp.all { it.isDigit() })
            _uiState.update { it.copy(buttonEnabled = it.timerActiveState) }
    }

    fun updateNumber(number: String) = _uiState.update { it.copy(number = number) }
}
