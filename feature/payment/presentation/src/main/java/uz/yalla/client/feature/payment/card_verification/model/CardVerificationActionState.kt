package uz.yalla.client.feature.payment.card_verification.model

internal sealed interface CardVerificationActionState {
    data object Loading : CardVerificationActionState
    data object VerificationSuccess : CardVerificationActionState
    data object ResendSuccess : CardVerificationActionState
    data object Error : CardVerificationActionState
}