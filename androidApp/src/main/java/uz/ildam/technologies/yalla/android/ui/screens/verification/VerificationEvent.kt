package uz.ildam.technologies.yalla.android.ui.screens.verification

import uz.ildam.technologies.yalla.feature.auth.domain.model.SendAuthCodeModel
import uz.ildam.technologies.yalla.feature.auth.domain.model.VerifyAuthCodeModel

sealed interface VerificationEvent {
    data object Loading : VerificationEvent
    data class Error(val error: String) : VerificationEvent
    data class SendSMSSuccess(val data: SendAuthCodeModel) : VerificationEvent
    data class VerifySuccess(val data: VerifyAuthCodeModel) : VerificationEvent
}