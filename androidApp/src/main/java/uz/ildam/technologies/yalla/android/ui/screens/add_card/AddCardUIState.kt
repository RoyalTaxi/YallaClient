package uz.ildam.technologies.yalla.android.ui.screens.add_card

data class AddCardUIState(
    val buttonState: Boolean = false,
    val cardNumber: String = "",
    val cardExpiry: String = ""
) {
    fun isNumberValid(): Boolean = cardNumber.length == 16 && cardNumber.all { it.isDigit() }
    fun isExpiryValid(): Boolean =
        cardExpiry.length == 4 && cardExpiry.all { it.isDigit() } && (cardExpiry.toIntOrNull()
            ?: 0) in 100..1299
}