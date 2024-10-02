package uz.ildam.technologies.yalla.android.ui.screens.credentials

data class CredentialsUIState(
    val number: String = "",
    val buttonEnabled: Boolean = false
) {
    fun isValidPhone() = number.all { it.isDigit() } && number.length == 9
    fun getFormattedPhone() = "998$number"
}