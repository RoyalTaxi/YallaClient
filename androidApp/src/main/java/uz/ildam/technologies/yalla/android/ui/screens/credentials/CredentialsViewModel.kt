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
import uz.ildam.technologies.yalla.android.utils.formatWithDotsDMY
import uz.ildam.technologies.yalla.core.data.local.AppPreferences
import uz.ildam.technologies.yalla.feature.auth.domain.usecase.register.RegisterUseCase

class CredentialsViewModel(
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
            ).onSuccess {
                AppPreferences.accessToken = it.accessToken
                AppPreferences.tokenType = it.tokenType
                AppPreferences.isDeviceRegistered = true
                AppPreferences.number = number
                AppPreferences.firstName = firstName
                AppPreferences.lastName = lastName
                AppPreferences.gender = gender.name
                AppPreferences.dateOfBirth = dateOfBirth.formatWithDotsDMY()
                _actionFlow.emit(CredentialsActionState.Success(it))
            }.onFailure {
                _actionFlow.emit(CredentialsActionState.Error)
            }
        }
    }
}