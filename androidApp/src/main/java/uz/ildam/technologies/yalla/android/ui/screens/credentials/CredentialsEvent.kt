package uz.ildam.technologies.yalla.android.ui.screens.credentials

import uz.ildam.technologies.yalla.feature.auth.domain.model.register.RegisterModel

internal sealed interface CredentialsEvent {
    data object Loading : CredentialsEvent
    data class Error(val error: String) : CredentialsEvent
    data class Success(val data: RegisterModel) : CredentialsEvent
}