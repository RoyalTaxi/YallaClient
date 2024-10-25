package uz.ildam.technologies.yalla.android.ui.screens.verification

import uz.ildam.technologies.yalla.feature.auth.domain.model.auth.SendAuthCodeModel
import uz.ildam.technologies.yalla.feature.auth.domain.model.auth.VerifyAuthCodeModel

sealed interface VerificationActionState {
    data object Loading : VerificationActionState
    data class Error(val error: String) : VerificationActionState
    data class SendSMSSuccess(val data: SendAuthCodeModel) : VerificationActionState
    data class VerifySuccess(val data: VerifyAuthCodeModel) : VerificationActionState
}