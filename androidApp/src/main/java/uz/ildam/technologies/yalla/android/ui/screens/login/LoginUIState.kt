package uz.ildam.technologies.yalla.android.ui.screens.login

data class LoginUIState(
    val buttonState: Boolean = false,
    val number: String = ""
) {
    fun getFormattedNumber() = "998$number"
}