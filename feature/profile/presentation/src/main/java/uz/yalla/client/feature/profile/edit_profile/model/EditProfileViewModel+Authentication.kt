package uz.yalla.client.feature.profile.edit_profile.model

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import uz.yalla.client.feature.profile.edit_profile.intent.EditProfileSideEffect

fun EditProfileViewModel.logout() = intent {
    viewModelScope.launch {
        logoutUseCase().onSuccess {
            appPreferences.performLogout()
            staticPreferences.performLogout()
            postSideEffect(EditProfileSideEffect.NavigateToLogin)
        }.onFailure(::handleException)
    }
}