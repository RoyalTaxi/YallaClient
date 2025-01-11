package uz.yalla.client.feature.android.auth.verification.model

import uz.ildam.technologies.yalla.feature.auth.domain.model.auth.SendAuthCodeModel
import uz.ildam.technologies.yalla.feature.auth.domain.model.auth.VerifyAuthCodeModel

sealed interface VerificationActionState {
    data object Loading : VerificationActionState
    data object Error : VerificationActionState
    data class SendSMSSuccess(val data: SendAuthCodeModel) : VerificationActionState
    data class VerifySuccess(val data: VerifyAuthCodeModel) : VerificationActionState
}