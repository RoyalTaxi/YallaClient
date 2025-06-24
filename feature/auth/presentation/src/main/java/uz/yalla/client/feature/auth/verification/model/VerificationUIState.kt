package uz.yalla.client.feature.auth.verification.model

data class VerificationUIState(
    val number: String = "",
    val code: String = "",
    val buttonState: Boolean = false,
    val hasRemainingTime: Boolean = false,
    val remainingMinutes: Int = 0,
    val remainingSeconds: Int = 0,
    val otpLength: Int = 5
)