package uz.yalla.client.feature.android.auth.verification.model

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
import uz.ildam.technologies.yalla.core.data.local.AppPreferences
import uz.ildam.technologies.yalla.feature.auth.domain.usecase.auth.SendCodeUseCase
import uz.ildam.technologies.yalla.feature.auth.domain.usecase.auth.VerifyCodeUseCase
import kotlin.time.Duration.Companion.seconds

class VerificationViewModel(
    private val verifyCodeUseCase: VerifyCodeUseCase,
    private val sendCodeUseCase: SendCodeUseCase
) : ViewModel() {

    private val _actionFlow = MutableSharedFlow<VerificationActionState>()
    val actionFlow = _actionFlow.asSharedFlow()

    private val _uiState = MutableStateFlow(VerificationUIState())
    val uiState = _uiState.asStateFlow()

    fun updateUiState(
        number: String? = null,
        code: String? = null,
        buttonState: Boolean? = null,
        hasRemainingTime: Boolean? = null,
        remainingMinutes: Int? = null,
        remainingSeconds: Int? = null
    ) = viewModelScope.launch {
        _uiState.update { currentState ->
            currentState.copy(
                number = number ?: currentState.number,
                code = code ?: currentState.code,
                buttonState = buttonState ?: currentState.buttonState,
                hasRemainingTime = hasRemainingTime ?: currentState.hasRemainingTime,
                remainingMinutes = remainingMinutes ?: currentState.remainingMinutes,
                remainingSeconds = remainingSeconds ?: currentState.remainingSeconds
            )
        }
    }

    fun verifyAuthCode() = viewModelScope.launch {
        _uiState.value.apply {
            _actionFlow.emit(VerificationActionState.Loading)

            verifyCodeUseCase(getFormattedNumber(), code.toInt())
                .onSuccess { result ->
                    result.client?.let { client ->
                        AppPreferences.accessToken = result.accessToken
                        AppPreferences.tokenType = result.tokenType
                        AppPreferences.isDeviceRegistered = true
                        AppPreferences.number = number
                        AppPreferences.gender = client.gender
                        AppPreferences.dateOfBirth = client.birthday
                        AppPreferences.firstName = client.givenNames
                        AppPreferences.lastName = client.surname
                    }
                    _actionFlow.emit(VerificationActionState.VerifySuccess(result))
                }
                .onFailure {
                    _actionFlow.emit(VerificationActionState.Error)
                }
        }
    }

    fun resendAuthCode() = viewModelScope.launch {
        _uiState.value.apply {
            _actionFlow.emit(VerificationActionState.Loading)
            sendCodeUseCase(getFormattedNumber())
                .onSuccess { result ->
                    _actionFlow.emit(
                        VerificationActionState.SendSMSSuccess(result)
                    )
                }
                .onFailure { _actionFlow.emit(VerificationActionState.Error) }
        }
    }

    fun countDownTimer(countDown: Int) = flow {
        for (seconds in countDown downTo 0) {
            delay(1.seconds)
            emit(seconds)
            if (!currentCoroutineContext().isActive) return@flow
        }
    }
}