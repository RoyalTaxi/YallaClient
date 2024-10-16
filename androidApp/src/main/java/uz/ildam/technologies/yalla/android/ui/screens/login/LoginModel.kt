package uz.ildam.technologies.yalla.android.ui.screens.login

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import uz.ildam.technologies.yalla.core.domain.model.Result
import uz.ildam.technologies.yalla.feature.auth.domain.usecase.auth.SendAuthCodeUseCase

class LoginModel(
    private val sendAuthCodeUseCase: SendAuthCodeUseCase
) : ScreenModel {

    private val eventChannel = Channel<LoginEvent>()
    val events = eventChannel.receiveAsFlow()

    fun sendAuthCode(number: String) {
        screenModelScope.launch {
            eventChannel.send(LoginEvent.Loading)
            when (val result = sendAuthCodeUseCase(number)) {
                is Result.Error -> eventChannel.send(LoginEvent.Error("Server Error"))
                is Result.Success -> eventChannel.send(LoginEvent.Success(result.data))
            }
        }
    }
}

