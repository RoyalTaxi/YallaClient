package uz.ildam.technologies.yalla.android.ui.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import uz.ildam.technologies.yalla.core.domain.error.Result
import uz.ildam.technologies.yalla.feature.auth.domain.usecase.auth.SendAuthCodeUseCase

class LoginViewModel(
    private val sendAuthCodeUseCase: SendAuthCodeUseCase
) : ViewModel() {

    private val eventChannel = Channel<LoginEvent>()
    val events = eventChannel.receiveAsFlow()

    fun sendAuthCode(number: String) = viewModelScope.launch {
        eventChannel.send(LoginEvent.Loading)
        when (val result = sendAuthCodeUseCase(number)) {
            is Result.Error -> eventChannel.send(LoginEvent.Error("Server Error"))
            is Result.Success -> eventChannel.send(LoginEvent.Success(result.data))
        }
    }
}

