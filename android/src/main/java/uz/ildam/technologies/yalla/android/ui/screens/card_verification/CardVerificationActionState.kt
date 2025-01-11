package uz.ildam.technologies.yalla.android.ui.screens.card_verification

sealed interface CardVerificationActionState {
    data object Loading : CardVerificationActionState
    data object VerificationSuccess : CardVerificationActionState
    data object ResendSuccess : CardVerificationActionState
    data object Error : CardVerificationActionState
}