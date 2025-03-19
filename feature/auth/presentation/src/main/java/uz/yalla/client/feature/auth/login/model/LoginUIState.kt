package uz.yalla.client.feature.auth.login.model

internal data class LoginUIState(
    val buttonState: Boolean = false,
    val number: String = ""
) {
    fun getFormattedNumber() = "998$number"
}