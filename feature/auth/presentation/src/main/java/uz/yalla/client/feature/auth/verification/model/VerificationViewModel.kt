package uz.yalla.client.feature.auth.verification.model

import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import uz.yalla.client.core.common.viewmodel.BaseViewModel
import uz.yalla.client.core.domain.local.AppPreferences
import uz.yalla.client.feature.auth.domain.model.auth.VerifyAuthCodeModel
import uz.yalla.client.feature.auth.domain.usecase.auth.SendCodeUseCase
import uz.yalla.client.feature.auth.domain.usecase.auth.VerifyCodeUseCase
import uz.yalla.client.feature.setting.domain.usecase.RefreshFCMTokenUseCase
import kotlin.time.Duration.Companion.seconds

class VerificationViewModel(
    private val prefs: AppPreferences,
    private val verifyCodeUseCase: VerifyCodeUseCase,
    private val sendCodeUseCase: SendCodeUseCase,
    private val refreshFCMTokenUseCase: RefreshFCMTokenUseCase
) : BaseViewModel() {

    private val _actionFlow = MutableSharedFlow<VerificationActionState>()
    val actionFlow = _actionFlow.asSharedFlow()

    private val _uiState = MutableStateFlow(VerificationUIState())
    val uiState = _uiState.asStateFlow()

    private val accessToken = MutableStateFlow("")

    init {
        viewModelScope.launch {
            prefs.accessToken.collectLatest { accessToken.value = it }
        }

        viewModelScope.launch {
            _uiState
                .distinctUntilChangedBy { it.code }
                .collectLatest {
                    if (it.code.length == it.otpLength) verifyAuthCode()
                }
        }
    }

    fun updateUiState(
        number: String? = null,
        code: String? = null,
        buttonState: Boolean? = null,
        hasRemainingTime: Boolean? = null,
        remainingMinutes: Int? = null,
        remainingSeconds: Int? = null
    ) = viewModelScope.launch {
        _uiState.update { current ->
            current.copy(
                number = number ?: current.number,
                code = code ?: current.code,
                buttonState = buttonState ?: current.buttonState,
                hasRemainingTime = hasRemainingTime ?: current.hasRemainingTime,
                remainingMinutes = remainingMinutes ?: current.remainingMinutes,
                remainingSeconds = remainingSeconds ?: current.remainingSeconds
            )
        }
    }

    fun verifyAuthCode() = viewModelScope.launchWithLoading {
        _uiState.value.let { state ->
            verifyCodeUseCase(state.number, state.code.toInt())
                .onSuccess { result ->
                    saveAuthResult(result)
                    refreshFCMToken()
                    _actionFlow.emit(VerificationActionState.VerifySuccess(result))
                }
                .onFailure(::handleException)
        }
    }

    fun resendAuthCode(hash: String?) = viewModelScope.launchWithLoading {
        _uiState.value.let { state ->
            sendCodeUseCase(state.number, hash)
                .onSuccess { result ->
                    _actionFlow.emit(VerificationActionState.SendSMSSuccess(result))
                }
                .onFailure(::handleException)
        }
    }

    fun countDownTimer(countDown: Int) = flow {
        for (sec in countDown downTo 0) {
            delay(1.seconds)
            emit(sec)
            if (!currentCoroutineContext().isActive) return@flow
        }
    }

    private fun refreshFCMToken() {
        viewModelScope.launch { refreshFCMTokenUseCase() }
    }

    private fun saveAuthResult(result: VerifyAuthCodeModel) {
        result.client?.let { client ->
            prefs.setAccessToken(result.accessToken)
            prefs.setTokenType(result.tokenType)
            prefs.setDeviceRegistered(true)
            prefs.setNumber(client.phone)
            prefs.setGender(client.gender)
            prefs.setDateOfBirth(client.birthday)
            prefs.setFirstName(client.givenNames)
            prefs.setLastName(client.surname)
        }
    }
}