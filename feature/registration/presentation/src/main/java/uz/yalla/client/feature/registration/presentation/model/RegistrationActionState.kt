package uz.yalla.client.feature.registration.presentation.model

import uz.yalla.client.feature.auth.domain.model.register.RegisterModel


internal sealed interface RegistrationActionState {
    data object Loading : RegistrationActionState
    data object Error : RegistrationActionState
    data class Success(val data: RegisterModel) : RegistrationActionState
}