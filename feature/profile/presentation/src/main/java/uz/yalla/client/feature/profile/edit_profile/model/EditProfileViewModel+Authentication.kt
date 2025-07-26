package uz.yalla.client.feature.profile.edit_profile.model

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

fun EditProfileViewModel.logout() = intent {
    viewModelScope.launch {
        logoutUseCase().onSuccess {
            prefs.performLogout()
            postSideEffect(EditProfileSideEffect.NavigateToStart)
        }.onFailure(::handleException)
    }
}