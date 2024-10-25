package uz.ildam.technologies.yalla.android.ui.screens.login

import uz.ildam.technologies.yalla.feature.auth.domain.model.auth.SendAuthCodeModel

sealed interface LoginActionState {
    data object Loading : LoginActionState
    data class Error(val error: String) : LoginActionState
    data class Success(val data: SendAuthCodeModel) : LoginActionState
}