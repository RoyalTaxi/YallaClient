package uz.yalla.client.feature.payment.add_card.model

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