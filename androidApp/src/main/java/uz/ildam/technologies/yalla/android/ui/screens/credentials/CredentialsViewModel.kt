package uz.ildam.technologies.yalla.android.ui.screens.credentials

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDate
import uz.ildam.technologies.yalla.android.utils.Utils.formatWithDotsDMY
import uz.ildam.technologies.yalla.core.data.local.AppPreferences
import uz.ildam.technologies.yalla.core.domain.error.Result
import uz.ildam.technologies.yalla.feature.auth.domain.usecase.register.RegisterUseCase

class CredentialsViewModel(
    private val registerUseCase: RegisterUseCase,
) : ViewModel() {

    private val _eventFlow = MutableSharedFlow<CredentialsActionState>()
    val eventFlow = _eventFlow.asSharedFlow()

    private val _uiState = MutableStateFlow(CredentialsUIState())
    val uiState = _uiState.asStateFlow()

    fun updateUiState(
        number: String? = null,
        secretKey: String? = null,
        firstName: String? = null,
        lastName: String? = null,
        gender: Gender? = null,
        dateOfBirth: LocalDate? = null
    ) = viewModelScope.launch {
        _uiState.update { currentState ->
            currentState.copy(
                number = number ?: currentState.number,
                secretKey = secretKey ?: currentState.secretKey,
                firstName = firstName ?: currentState.firstName,
                lastName = lastName ?: currentState.lastName,
                gender = gender ?: currentState.gender,
                dateOfBirth = dateOfBirth ?: currentState.dateOfBirth
            )
        }
    }


    fun register() = viewModelScope.launch {
        _uiState.value.apply {
            when (
                val result = registerUseCase(
                    number,
                    firstName,
                    lastName,
                    gender.name,
                    dateOfBirth.formatWithDotsDMY(),
                    secretKey
                )
            ) {
                is Result.Error -> _eventFlow.emit(CredentialsActionState.Error("server error"))

                is Result.Success -> {
                    AppPreferences.accessToken = result.data.accessToken
                    AppPreferences.tokenType = result.data.accessToken
                    AppPreferences.isDeviceRegistered = true
                    AppPreferences.number = number
                    AppPreferences.firstName = firstName
                    AppPreferences.lastName = lastName
                    AppPreferences.gender = gender.name
                    AppPreferences.dateOfBirth = dateOfBirth.formatWithDotsDMY()
                    _eventFlow.emit(CredentialsActionState.Success(result.data))
                }
            }
        }
    }
}