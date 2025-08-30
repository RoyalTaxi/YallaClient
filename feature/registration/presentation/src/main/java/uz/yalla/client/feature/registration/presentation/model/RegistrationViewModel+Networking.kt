package uz.yalla.client.feature.registration.presentation.model

import androidx.lifecycle.viewModelScope
import uz.yalla.client.core.common.formation.formatWithDotsDMY
import uz.yalla.client.feature.registration.presentation.intent.RegistrationSideEffect

internal fun RegistrationViewModel.register() = intent {
    viewModelScope.launchWithLoading{
        intent {
            registerUseCase(
                phone = state.formattedNumber(),
                firstName = state.firstName,
                lastName = state.lastName,
                gender = state.gender.name,
                dateOfBirth = state.dateOfBirth.formatWithDotsDMY(),
                key = state.secretKey
            ).onSuccess { result ->
                staticPreferences.isDeviceRegistered = true
                appPreferences.setAccessToken(result.accessToken)
                appPreferences.setNumber(state.phoneNumber)
                appPreferences.setFirstName(state.firstName)
                appPreferences.setLastName(state.lastName)
                appPreferences.setGender(state.gender.name)
                appPreferences.setDateOfBirth(state.dateOfBirth.formatWithDotsDMY())
                postSideEffect(RegistrationSideEffect.NavigateToMap)
            }.onFailure(::handleException)
        }
    }
}