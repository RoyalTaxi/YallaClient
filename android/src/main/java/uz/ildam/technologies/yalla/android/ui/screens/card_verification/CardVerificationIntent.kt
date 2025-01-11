package uz.ildam.technologies.yalla.android.ui.screens.card_verification

sealed interface CardVerificationIntent {
    data object NavigateBack : CardVerificationIntent
    data class SetCode(val code: String) : CardVerificationIntent
    data object ResendCode : CardVerificationIntent
    data object VerifyCode : CardVerificationIntent
}