package uz.yalla.client.feature.android.payment.card_verification.view

internal sealed interface CardVerificationIntent {
    data object NavigateBack : CardVerificationIntent
    data class SetCode(val code: String) : CardVerificationIntent
    data object ResendCode : CardVerificationIntent
    data object VerifyCode : CardVerificationIntent
}