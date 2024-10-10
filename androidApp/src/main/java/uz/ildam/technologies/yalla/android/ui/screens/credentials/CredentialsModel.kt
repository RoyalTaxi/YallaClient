package uz.ildam.technologies.yalla.android.ui.screens.credentials

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDate
import uz.ildam.technologies.yalla.android.utils.Utils.formatWithDotsDMY
import uz.ildam.technologies.yalla.core.data.local.AppPreferences
import uz.ildam.technologies.yalla.feature.auth.domain.usecase.register.RegisterUseCase

class CredentialsModel(
    private val registerUseCase: RegisterUseCase,
) : ScreenModel {
    private val _uiState = MutableStateFlow(CredentialsUIState())
    val uiState = _uiState.asStateFlow()

    fun register() = screenModelScope.launch {
        uiState.value.apply {
            registerUseCase(
                number, name, surname, gender, dateOfBirth.formatWithDotsDMY(), secretKey
            ).onSuccess {
                AppPreferences.isDeviceRegistered = true
                AppPreferences.number = number
                AppPreferences.firstName = name
                AppPreferences.lastName = surname
                AppPreferences.gender = gender
                AppPreferences.dateOfBirth = dateOfBirth.formatWithDotsDMY()
            }
        }
    }

    fun updateName(name: String) = updateUIState { _uiState.update { it.copy(name = name) } }

    fun updateSurname(surname: String) =
        updateUIState { _uiState.update { it.copy(surname = surname) } }

    fun updateGender(gender: String) =
        updateUIState { _uiState.update { it.copy(gender = gender) } }

    fun updateSecretKey(secretKey: String) =
        updateUIState { _uiState.update { it.copy(secretKey = secretKey) } }

    fun updateNumber(number: String) =
        updateUIState { _uiState.update { it.copy(number = number) } }

    fun updateDateOfBirth(dateOfBirth: LocalDate) =
        updateUIState { _uiState.update { it.copy(dateOfBirth = dateOfBirth) } }

    private fun updateUIState(update: () -> Unit) = screenModelScope.launch {
        update()
        _uiState.update {
            it.copy(
                buttonEnabled = uiState.value.gender != "NOT_SELECTED" &&
                        uiState.value.name.isNotBlank() &&
                        uiState.value.surname.isNotBlank() &&
                        uiState.value.dateOfBirth != null &&
                        uiState.value.number.isNotBlank() &&
                        uiState.value.secretKey.isNotBlank()
            )
        }
    }
}