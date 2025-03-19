package uz.yalla.client.feature.auth.login.model

import uz.yalla.client.feature.auth.domain.model.auth.SendAuthCodeModel

internal sealed interface LoginActionState {
    data object Loading : LoginActionState
    data object Error : LoginActionState
    data class Success(val data: SendAuthCodeModel) : LoginActionState
}