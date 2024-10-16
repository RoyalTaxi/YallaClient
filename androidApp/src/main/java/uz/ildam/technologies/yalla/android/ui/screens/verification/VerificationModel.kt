package uz.ildam.technologies.yalla.android.ui.screens.verification

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import uz.ildam.technologies.yalla.core.domain.model.Result
import uz.ildam.technologies.yalla.feature.auth.domain.usecase.auth.SendAuthCodeUseCase
import uz.ildam.technologies.yalla.feature.auth.domain.usecase.auth.VerifyAuthCodeUseCase
import kotlin.time.Duration.Companion.seconds

class VerificationModel(
    private val verifyAuthCodeUseCase: VerifyAuthCodeUseCase,
    private val sendAuthCodeUseCase: SendAuthCodeUseCase
) : ScreenModel {

    private val eventChannel = Channel<VerificationEvent>()
    val events = eventChannel.receiveAsFlow()

    fun verifyAuthCode(number: String, code: Int) = screenModelScope.launch {
        eventChannel.send(VerificationEvent.Loading)
        when (val result = verifyAuthCodeUseCase(number, code)) {
            is Result.Error -> eventChannel.send(VerificationEvent.Error("Server error"))
            is Result.Success -> eventChannel.send(VerificationEvent.VerifySuccess(result.data))
        }
    }

    fun resendAuthCode(number: String) = screenModelScope.launch {
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
