package uz.ildam.technologies.yalla.android.ui.screens.credentials

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import uz.ildam.technologies.yalla.feature.auth.domain.usecase.SendAuthCodeUseCase

class CredentialsModel(
    private val sendAuthCodeUseCase: SendAuthCodeUseCase
) : ScreenModel {

    private val _uiState = MutableStateFlow(CredentialsUIState())
    val uiState = _uiState.asStateFlow()

    private val validationChannel = Channel<Unit>(Channel.BUFFERED)
    val validationFlow = validationChannel.receiveAsFlow()

    fun sendAuthCode() {
        screenModelScope.launch {
            sendAuthCodeUseCase(uiState.value.getFormattedPhone())
                .onSuccess {
                    validationChannel.send(Unit)
                }
        }
    }

    fun updateNumber(number: String) {
        if (number.all { it.isDigit() } && number.length <= 9) _uiState.update { it.copy(number = number) }
        if (uiState.value.isValidPhone()) _uiState.update { it.copy(buttonEnabled = true) }
    }
}