package uz.ildam.technologies.yalla.android.ui.screens.verification

data class ValidationUIState(
    val number: String = "",
    val otp: String = "",
    val buttonEnabled: Boolean = false,
    val resendInSecondsText: String = "",
    val timerActiveState: Boolean = true,
    val secretKey: String = ""
) {
    fun getFormattedNumber() = "998$number"
}