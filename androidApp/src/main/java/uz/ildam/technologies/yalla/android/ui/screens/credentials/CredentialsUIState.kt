package uz.ildam.technologies.yalla.android.ui.screens.credentials

import org.threeten.bp.LocalDate

data class CredentialsUIState(
    val number: String = "",
    val name: String = "",
    val surname: String = "",
    val dateOfBirth: LocalDate? = null,
    val secretKey: String = "",
    val gender: String = "NOT_SELECTED",
    val buttonEnabled: Boolean = false
)
