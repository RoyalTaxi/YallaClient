package uz.yalla.client.feature.registration.presentation.model

import uz.yalla.client.feature.registration.presentation.intent.RegistrationIntent
import uz.yalla.client.feature.registration.presentation.intent.RegistrationSideEffect

internal fun RegistrationViewModel.onIntent(intent: RegistrationIntent) = intent {
    when (intent) {
        is RegistrationIntent.SetDateOfBirth -> setDateOfBirth(intent.dateOfBirth)
        is RegistrationIntent.SetFirstName -> setFirstName(intent.firstName)
        is RegistrationIntent.SetGender -> setGender(intent.gender)
        is RegistrationIntent.SetLastName -> setLastName(intent.lastName)
        RegistrationIntent.CloseDateBottomSheet -> setDatePickerVisible(false)
        RegistrationIntent.NavigateBack -> postSideEffect(RegistrationSideEffect.NavigateBack)
        RegistrationIntent.Register -> register()
        RegistrationIntent.OpenDateBottomSheet -> {
            setDatePickerVisible(true)
            postSideEffect(RegistrationSideEffect.ClearFocus)
        }
    }
}