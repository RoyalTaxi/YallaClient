package uz.yalla.client.feature.registration.presentation.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDate
import uz.yalla.client.core.common.formation.formatWithDotsDMY
import uz.yalla.client.core.domain.local.AppPreferences
import uz.yalla.client.feature.auth.domain.usecase.register.RegisterUseCase

internal class RegistrationViewModel(
    private val registerUseCase: RegisterUseCase,
    private val prefs: AppPreferences
) : ViewModel() {

    private val _actionFlow = MutableSharedFlow<RegistrationActionState>()
    val actionFlow = _actionFlow.asSharedFlow()

    private val _uiState = MutableStateFlow(RegistrationUIState())
    val uiState = _uiState.asStateFlow()

    fun updateUiState(
        number: String? = null,
        secretKey: String? = null,
        firstName: String? = null,
        lastName: String? = null,
        gender: Gender? = null,
        dateOfBirth: LocalDate? = null
    ) = viewModelScope.launch(Dispatchers.IO) {
        _uiState.update { current ->
            current.copy(
                number = number ?: current.number,
                secretKey = secretKey ?: current.secretKey,
                firstName = firstName ?: current.firstName,
                lastName = lastName ?: current.lastName,
                gender = gender ?: current.gender,
                dateOfBirth = dateOfBirth ?: current.dateOfBirth
            )
        }
    }

    fun register() = viewModelScope.launch(Dispatchers.IO) {
        _actionFlow.emit(RegistrationActionState.Loading)
        _uiState.value.apply {
            registerUseCase(
                formattedNumber(),
                firstName,
                lastName,
                gender.name,
                dateOfBirth.formatWithDotsDMY(),
                secretKey
            ).onSuccess { result ->
                prefs.setAccessToken(result.accessToken)
                prefs.setTokenType(result.tokenType)
                prefs.setDeviceRegistered(true)
                prefs.setNumber(number)
                prefs.setFirstName(firstName)
                prefs.setLastName(lastName)
                prefs.setGender(gender.name)
                prefs.setDateOfBirth(dateOfBirth.formatWithDotsDMY())
                _actionFlow.emit(RegistrationActionState.Success(result))
            }.onFailure {
                _actionFlow.emit(RegistrationActionState.Error)
            }
        }
    }

    fun setDatePickerVisible(visible: Boolean) {
        _uiState.update { it.copy(isDatePickerVisible = visible) }
    }
}