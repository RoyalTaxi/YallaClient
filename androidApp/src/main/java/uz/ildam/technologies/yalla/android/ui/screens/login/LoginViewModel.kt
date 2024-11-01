package uz.ildam.technologies.yalla.android.ui.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import uz.ildam.technologies.yalla.core.domain.error.Result
import uz.ildam.technologies.yalla.feature.auth.domain.usecase.auth.SendAuthCodeUseCase

class LoginViewModel(
    private val sendAuthCodeUseCase: SendAuthCodeUseCase
) : ViewModel() {

    private val _eventFlow = MutableSharedFlow<LoginActionState>()
    val eventFlow = _eventFlow.asSharedFlow()

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
        _eventFlow.emit(LoginActionState.Loading)
        when (val result = sendAuthCodeUseCase(_uiState.value.getFormattedNumber())) {
            is Result.Error -> _eventFlow.emit(LoginActionState.Error("Server Error"))
            is Result.Success -> _eventFlow.emit(LoginActionState.Success(result.data))
        }
    }
}

