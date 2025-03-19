package uz.yalla.client.feature.payment.add_card.model

internal sealed interface AddCardActionState {
    data object Loading : AddCardActionState
    data object Error : AddCardActionState
    data class Success(
        val key: String,
        val cardNumber: String,
        val cardExpiry: String
    ) : AddCardActionState
}