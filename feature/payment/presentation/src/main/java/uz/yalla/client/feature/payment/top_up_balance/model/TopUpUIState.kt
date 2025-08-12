package uz.yalla.client.feature.payment.top_up_balance.model

data class TopUpUIState(
    val topUpAmount: String = ""
) {
    val isPayButtonValid: Boolean get() = topUpAmount.isNotBlank()
}