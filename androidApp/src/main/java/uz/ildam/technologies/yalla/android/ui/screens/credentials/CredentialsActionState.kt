package uz.ildam.technologies.yalla.android.ui.screens.credentials

import uz.ildam.technologies.yalla.feature.auth.domain.model.register.RegisterModel

sealed interface CredentialsActionState {
    data object Loading : CredentialsActionState
    data class Error(val error: String) : CredentialsActionState
    data class Success(val data: RegisterModel) : CredentialsActionState
}