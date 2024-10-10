package uz.ildam.technologies.yalla.android.ui.screens.login

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import uz.ildam.technologies.yalla.feature.auth.domain.usecase.auth.SendAuthCodeUseCase

class LoginModel(
    private val sendAuthCodeUseCase: SendAuthCodeUseCase
) : ScreenModel {

    private val _uiState = MutableStateFlow(LoginUIState())
    val uiState = _uiState.asStateFlow()

    private val validationChannel = Channel<Unit>(Channel.BUFFERED)
    val validationFlow = validationChannel.receiveAsFlow()

    fun sendAuthCode() {
        _uiState.update { it.copy(buttonEnabled = false) }
        screenModelScope.launch {
            sendAuthCodeUseCase(uiState.value.getFormattedPhone())
                .onSuccess { data ->
                    _uiState.update { it.copy(time = data.time) }
                    validationChannel.send(Unit)
                }
        }.invokeOnCompletion {
            _uiState.update { it.copy(buttonEnabled = true) }
        }
    }

    fun updateNumber(number: String) {
        if (number.all { it.isDigit() } && number.length <= 9) _uiState.update {
            it.copy(
                buttonEnabled = number.length == 9,
                number = number
            )
        }
    }
}