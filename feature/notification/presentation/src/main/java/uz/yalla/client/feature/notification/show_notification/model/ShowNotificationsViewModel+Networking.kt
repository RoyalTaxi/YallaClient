package uz.yalla.client.feature.notification.show_notification.model

import androidx.lifecycle.viewModelScope

internal fun ShowNotificationViewModel.getNotification(id: Int) = intent {
    viewModelScope.launchWithLoading {
        getNotificationUseCase(id)
            .onSuccess { result ->
                reduce {
                    state.copy(notification = result)
                }
            }
            .onFailure(::handleException)
    }
}