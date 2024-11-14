package uz.ildam.technologies.yalla.android.ui.screens.credentials

import org.threeten.bp.LocalDate

data class CredentialsUIState(
    val number: String = "",
    val secretKey: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val dateOfBirth: LocalDate? = null,
    val gender: Gender = Gender.NOT_SELECTED
) {
    fun formattedNumber() = "998$number"
}