package uz.yalla.client.feature.registration.presentation.view

import org.threeten.bp.LocalDate
import uz.yalla.client.feature.registration.presentation.model.Gender


internal sealed interface RegistrationIntent {
    data object OpenDateBottomSheet : RegistrationIntent
    data class SetFirstName(val firstName: String) : RegistrationIntent
    data class SetLastName(val lastName: String) : RegistrationIntent
    data class SetDateOfBirth(val dateOfBirth: LocalDate) : RegistrationIntent
    data class SetGender(val gender: Gender) : RegistrationIntent
    data object CloseDateBottomSheet : RegistrationIntent
    data object NavigateBack : RegistrationIntent
    data object Register : RegistrationIntent
}