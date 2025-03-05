package uz.yalla.client.feature.android.registration.credentials.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDate
import uz.yalla.client.core.common.formation.formatWithDotsDMY
import uz.yalla.client.core.data.local.AppPreferences
import uz.yalla.client.feature.auth.domain.usecase.register.RegisterUseCase

internal class CredentialsViewModel(
    private val registerUseCase: RegisterUseCase,
) : ViewModel() {

    private val _actionFlow = MutableSharedFlow<CredentialsActionState>()
    val actionFlow = _actionFlow.asSharedFlow()

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
        _actionFlow.emit(CredentialsActionState.Loading)
        _uiState.value.apply {
            registerUseCase(
                formattedNumber(),
                firstName,
                lastName,
                gender.name,
                dateOfBirth.formatWithDotsDMY(),
                secretKey
            ).onSuccess { result ->
                AppPreferences.accessToken = result.accessToken
                AppPreferences.tokenType = result.tokenType
                AppPreferences.isDeviceRegistered = true
                AppPreferences.number = number
                AppPreferences.firstName = firstName
                AppPreferences.lastName = lastName
                AppPreferences.gender = gender.name
                AppPreferences.dateOfBirth = dateOfBirth.formatWithDotsDMY()
                _actionFlow.emit(CredentialsActionState.Success(result))
            }.onFailure {
                _actionFlow.emit(CredentialsActionState.Error)
            }
        }
    }
}