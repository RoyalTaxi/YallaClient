package uz.yalla.client.feature.auth.verification.view

sealed class VerificationIntent {
    data object NavigateBack : VerificationIntent()
    data class VerifyCode(val number: String, val code: String) : VerificationIntent()
    data class ResendCode(val number: String) : VerificationIntent()
    data class SetCode(val code: String) : VerificationIntent()
}