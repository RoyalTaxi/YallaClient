package uz.yalla.client.feature.android.registration.credentials.model

import org.threeten.bp.LocalDate

internal data class CredentialsUIState(
    val number: String = "",
    val secretKey: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val dateOfBirth: LocalDate? = null,
    val gender: Gender = Gender.NOT_SELECTED
) {
    fun formattedNumber() = "998$number"
}