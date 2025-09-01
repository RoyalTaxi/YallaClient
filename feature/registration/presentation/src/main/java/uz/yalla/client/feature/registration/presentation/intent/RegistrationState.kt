package uz.yalla.client.feature.registration.presentation.intent

import org.threeten.bp.LocalDate
import uz.yalla.client.feature.registration.presentation.model.Gender

internal data class RegistrationState(
    val secretKey: String,
    val phoneNumber: String,
    val firstName: String,
    val lastName: String,
    val isDatePickerVisible: Boolean,
    val dateOfBirth: LocalDate?,
    val gender: Gender
) {
    fun formattedNumber() = "998$phoneNumber"

    companion object {
        val INITIAL = RegistrationState(
            phoneNumber = "",
            secretKey = "",
            firstName = "",
            lastName = "",
            isDatePickerVisible = false,
            dateOfBirth = null,
            gender = Gender.NOT_SELECTED
        )
    }
}