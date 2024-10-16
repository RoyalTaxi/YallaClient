package uz.ildam.technologies.yalla.android.ui.screens.login

import uz.ildam.technologies.yalla.feature.auth.domain.model.SendAuthCodeModel

sealed interface LoginEvent {
    data object Loading : LoginEvent
    data class Error(val error: String) : LoginEvent
    data class Success(val data: SendAuthCodeModel) : LoginEvent
}