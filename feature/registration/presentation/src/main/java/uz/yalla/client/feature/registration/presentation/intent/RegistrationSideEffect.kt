package uz.yalla.client.feature.registration.presentation.intent

sealed interface RegistrationSideEffect {
    data object NavigateBack: RegistrationSideEffect
    data object NavigateToMap: RegistrationSideEffect
    data object ClearFocus: RegistrationSideEffect
}