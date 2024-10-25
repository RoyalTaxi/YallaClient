package uz.ildam.technologies.yalla.android.ui.screens.credentials

import org.threeten.bp.LocalDate

sealed interface CredentialsIntent {
    data object OpenDateBottomSheet : CredentialsIntent
    data class SetFirstName(val firstName: String) : CredentialsIntent
    data class SetLastName(val lastName: String) : CredentialsIntent
    data class SetDateOfBirth(val dateOfBirth: LocalDate) : CredentialsIntent
    data class SetGender(val gender: Gender) : CredentialsIntent
    data object CloseDateBottomSheet : CredentialsIntent
    data object NavigateBack : CredentialsIntent
    data object Register : CredentialsIntent
}