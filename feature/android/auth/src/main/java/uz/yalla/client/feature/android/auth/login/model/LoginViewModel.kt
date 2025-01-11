package uz.yalla.client.feature.android.auth.login.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import uz.ildam.technologies.yalla.feature.auth.domain.usecase.auth.SendCodeUseCase

internal class LoginViewModel(
    private val sendCodeUseCase: SendCodeUseCase
) : ViewModel() {

    private val _actionFlow = MutableSharedFlow<LoginActionState>()
    val actionFlow = _actionFlow.asSharedFlow()

    private val _uiState = MutableStateFlow(LoginUIState())
    val uiState = _uiState.asStateFlow()

    fun setNumber(number: String) = viewModelScope.launch {
        if (number.length <= 9) _uiState.update { it.copy(number = number) }
        if (number.length == 9) _uiState.update { it.copy(buttonState = true) }
    }

    fun setButtonState(buttonState: Boolean) = viewModelScope.launch {
        _uiState.update { it.copy(buttonState = buttonState) }
    }


    fun sendAuthCode() = viewModelScope.launch {
        _actionFlow.emit(LoginActionState.Loading)
        sendCodeUseCase(uiState.value.getFormattedNumber())
            .onSuccess { result -> _actionFlow.emit(LoginActionState.Success(result)) }
            .onFailure { _actionFlow.emit(LoginActionState.Error) }
    }
}
