package uz.yalla.client.feature.auth.verification.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import uz.yalla.client.core.data.local.AppPreferences
import uz.yalla.client.feature.auth.domain.model.auth.VerifyAuthCodeModel
import uz.yalla.client.feature.auth.domain.usecase.auth.SendCodeUseCase
import uz.yalla.client.feature.auth.domain.usecase.auth.VerifyCodeUseCase
import uz.yalla.client.feature.setting.domain.usecase.SendFCMTokenUseCase
import kotlin.time.Duration.Companion.seconds

class VerificationViewModel(
    private val prefs: uz.yalla.client.core.domain.local.AppPreferences,
    private val verifyCodeUseCase: VerifyCodeUseCase,
    private val sendCodeUseCase: SendCodeUseCase,
    private val sendFCMTokenUseCase: SendFCMTokenUseCase,
) : ViewModel() {

    private val _actionFlow = MutableSharedFlow<VerificationActionState>()
    val actionFlow = _actionFlow.asSharedFlow()

    private val _uiState = MutableStateFlow(VerificationUIState())
    val uiState = _uiState.asStateFlow()

    private val accessToken = MutableStateFlow("")

    init {
        viewModelScope.launch {
            prefs.accessToken.collectLatest { accessToken.value = it }
        }
    }

    fun updateUiState(
        number: String? = null,
        code: String? = null,
        buttonState: Boolean? = null,
        hasRemainingTime: Boolean? = null,
        remainingMinutes: Int? = null,
        remainingSeconds: Int? = null
    ) = viewModelScope.launch(Dispatchers.IO) {
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

    fun verifyAuthCode() = viewModelScope.launch(Dispatchers.IO) {
        _uiState.value.apply {
            _actionFlow.emit(VerificationActionState.Loading)

            verifyCodeUseCase(getFormattedNumber(), code.toInt())
                .onSuccess { result ->
                    saveAuthResult(result)
                    getFCMToken()
                    _actionFlow.emit(VerificationActionState.VerifySuccess(result))
                }
                .onFailure {
                    _actionFlow.emit(VerificationActionState.Error)
                }
        }
    }

    fun resendAuthCode(hash: String?) = viewModelScope.launch(Dispatchers.IO) {
        _uiState.value.apply {
            _actionFlow.emit(VerificationActionState.Loading)
            sendCodeUseCase(getFormattedNumber(), hash)
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

    private fun getFCMToken() {
        viewModelScope.launch(Dispatchers.IO) {
            FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    AppPreferences.firebaseToken = task.result
                    if (accessToken.value.isNotBlank()) sendFCMToken(task.result)
                }
            }
        }
    }

    private fun sendFCMToken(token: String) {
        viewModelScope.launch(Dispatchers.IO) {
            sendFCMTokenUseCase(token)
        }
    }

    private fun saveAuthResult(result: VerifyAuthCodeModel) {
        result.client?.let { client ->
            prefs.setAccessToken(result.accessToken)
            prefs.setTokenType(result.tokenType)


            AppPreferences.isDeviceRegistered = true
            AppPreferences.number = client.phone
            AppPreferences.gender = client.gender
            AppPreferences.dateOfBirth = client.birthday
            AppPreferences.firstName = client.givenNames
            AppPreferences.lastName = client.surname
        }
    }
}