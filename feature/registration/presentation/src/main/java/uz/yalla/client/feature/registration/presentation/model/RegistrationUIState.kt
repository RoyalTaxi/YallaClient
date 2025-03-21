package uz.yalla.client.feature.registration.presentation.model

import org.threeten.bp.LocalDate

internal data class RegistrationUIState(
    val number: String = "",
    val secretKey: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val isDatePickerVisible: Boolean = false,
    val dateOfBirth: LocalDate? = null,
    val gender: Gender = Gender.NOT_SELECTED
) {
    fun formattedNumber() = "998$number"
}