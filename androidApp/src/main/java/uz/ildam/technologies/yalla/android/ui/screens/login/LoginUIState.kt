package uz.ildam.technologies.yalla.android.ui.screens.login

data class LoginUIState(
    val number: String = "",
    val time: Int = 0,
    val buttonEnabled: Boolean = false
) {
    fun getFormattedPhone() = "998$number"
}