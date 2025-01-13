package uz.yalla.client.feature.android.registration.credentials.model

import uz.ildam.technologies.yalla.feature.auth.domain.model.register.RegisterModel

internal sealed interface CredentialsActionState {
    data object Loading : CredentialsActionState
    data object Error : CredentialsActionState
    data class Success(val data: RegisterModel) : CredentialsActionState
}