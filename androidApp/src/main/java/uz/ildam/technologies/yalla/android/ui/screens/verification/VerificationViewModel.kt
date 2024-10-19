package uz.ildam.technologies.yalla.android.ui.screens.verification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import uz.ildam.technologies.yalla.core.data.local.AppPreferences
import uz.ildam.technologies.yalla.core.domain.error.Result
import uz.ildam.technologies.yalla.feature.auth.domain.usecase.auth.SendAuthCodeUseCase
import uz.ildam.technologies.yalla.feature.auth.domain.usecase.auth.VerifyAuthCodeUseCase
import kotlin.time.Duration.Companion.seconds

internal class VerificationViewModel(
    private val verifyAuthCodeUseCase: VerifyAuthCodeUseCase,
    private val sendAuthCodeUseCase: SendAuthCodeUseCase
) : ViewModel() {

    private val eventChannel = Channel<VerificationEvent>()
    val events = eventChannel.receiveAsFlow()

    fun verifyAuthCode(number: String, code: Int) = viewModelScope.launch {
        eventChannel.send(VerificationEvent.Loading)
        when (val result = verifyAuthCodeUseCase(number, code)) {
            is Result.Error -> eventChannel.send(VerificationEvent.Error("Server error"))
            is Result.Success -> {
                eventChannel.send(VerificationEvent.VerifySuccess(result.data))
                result.data.client?.let { client ->
                    AppPreferences.accessToken = result.data.accessToken
                    AppPreferences.tokenType = result.data.tokenType
                    AppPreferences.isDeviceRegistered = true
                    AppPreferences.number = number
                    AppPreferences.gender = client.gender
                    AppPreferences.dateOfBirth = client.birthday
                    AppPreferences.firstName = client.givenNames
                    AppPreferences.lastName = client.surname
                }
            }
        }
    }

    fun resendAuthCode(number: String) = viewModelScope.launch {
        eventChannel.send(VerificationEvent.Loading)
        when (val result = sendAuthCodeUseCase(number)) {
            is Result.Error -> eventChannel.send(VerificationEvent.Error("server error"))
            is Result.Success -> eventChannel.send(VerificationEvent.SendSMSSuccess(result.data))
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
