package uz.ildam.technologies.yalla.android.ui.screens.verification

sealed interface VerificationIntent {
    data object NavigateBack : VerificationIntent
    data object ClearFocus : VerificationIntent
    data class SetCode(val code: String) : VerificationIntent
    data class ResendCode(val number: String) : VerificationIntent
    data class VerifyCode(val number: String, val code: String) : VerificationIntent
}