package uz.ildam.technologies.yalla.android.ui.screens.card_verification

data class CardVerificationUIState(
    val key: String = "",
    val cardNumber: String = "",
    val cardExpiry: String = "",
    val code: String = "",
    val buttonState: Boolean = false,
    val hasRemainingTime: Boolean = false,
    val remainingMinutes: Int = 0,
    val remainingSeconds: Int = 0,
)