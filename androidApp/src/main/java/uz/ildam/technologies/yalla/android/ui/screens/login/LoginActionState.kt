package uz.ildam.technologies.yalla.android.ui.screens.login

import uz.ildam.technologies.yalla.feature.auth.domain.model.auth.SendAuthCodeModel

sealed interface LoginActionState {
    data object Loading : LoginActionState
    data object Error : LoginActionState
    data class Success(val data: SendAuthCodeModel) : LoginActionState
}