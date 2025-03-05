package uz.yalla.client.feature.android.registration.credentials.view

import org.threeten.bp.LocalDate
import uz.yalla.client.feature.android.registration.credentials.model.Gender


internal sealed interface CredentialsIntent {
    data object OpenDateBottomSheet : CredentialsIntent
    data class SetFirstName(val firstName: String) : CredentialsIntent
    data class SetLastName(val lastName: String) : CredentialsIntent
    data class SetDateOfBirth(val dateOfBirth: LocalDate) : CredentialsIntent
    data class SetGender(val gender: Gender) : CredentialsIntent
    data object CloseDateBottomSheet : CredentialsIntent
    data object NavigateBack : CredentialsIntent
    data object Register : CredentialsIntent
}