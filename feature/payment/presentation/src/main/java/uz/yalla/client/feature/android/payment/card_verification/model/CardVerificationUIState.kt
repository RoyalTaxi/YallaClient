package uz.yalla.client.feature.android.payment.card_verification.model

internal data class CardVerificationUIState(
    val key: String = "",
    val cardNumber: String = "",
    val cardExpiry: String = "",
    val code: String = "",
    val buttonState: Boolean = false,
    val hasRemainingTime: Boolean = false,
    val remainingMinutes: Int = 0,
    val remainingSeconds: Int = 0,
)