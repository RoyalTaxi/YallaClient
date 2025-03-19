package uz.yalla.client.feature.auth.verification.model

import uz.yalla.client.feature.auth.domain.model.auth.SendAuthCodeModel
import uz.yalla.client.feature.auth.domain.model.auth.VerifyAuthCodeModel

sealed interface VerificationActionState {
    data object Loading : VerificationActionState
    data object Error : VerificationActionState
    data class SendSMSSuccess(val data: SendAuthCodeModel) : VerificationActionState
    data class VerifySuccess(val data: VerifyAuthCodeModel) : VerificationActionState
}