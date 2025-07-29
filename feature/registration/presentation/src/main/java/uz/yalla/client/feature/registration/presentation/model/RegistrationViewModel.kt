package uz.yalla.client.feature.registration.presentation.model

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDate
import uz.yalla.client.core.common.formation.formatWithDotsDMY
import uz.yalla.client.core.common.viewmodel.BaseViewModel
import uz.yalla.client.core.domain.local.AppPreferences
import uz.yalla.client.core.domain.local.StaticPreferences
import uz.yalla.client.feature.auth.domain.usecase.register.RegisterUseCase

internal class RegistrationViewModel(
    private val registerUseCase: RegisterUseCase,
    private val appPreferences: AppPreferences,
    private val staticPreferences: StaticPreferences
) : BaseViewModel() {

    private val _uiState = MutableStateFlow(RegistrationUIState())
    val uiState = _uiState.asStateFlow()

    private val _navigationChannel: Channel<Unit> = Channel(Channel.CONFLATED)
    val navigationChannel = _navigationChannel.receiveAsFlow()

    fun updateUiState(
        number: String? = null,
        secretKey: String? = null,
        firstName: String? = null,
        lastName: String? = null,
        gender: Gender? = null,
        dateOfBirth: LocalDate? = null
    ) = viewModelScope.launch {
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

    fun register() = viewModelScope.launchWithLoading {
        _uiState.value.apply {
            registerUseCase(
                formattedNumber(),
                firstName,
                lastName,
                gender.name,
                dateOfBirth.formatWithDotsDMY(),
                secretKey
            ).onSuccess { result ->
                staticPreferences.isDeviceRegistered = true
                appPreferences.setAccessToken(result.accessToken)
                appPreferences.setNumber(number)
                appPreferences.setFirstName(firstName)
                appPreferences.setLastName(lastName)
                appPreferences.setGender(gender.name)
                appPreferences.setDateOfBirth(dateOfBirth.formatWithDotsDMY())
                _navigationChannel.send(Unit)
            }.onFailure(::handleException)
        }
    }

    fun setDatePickerVisible(visible: Boolean) {
        _uiState.update { it.copy(isDatePickerVisible = visible) }
    }
}