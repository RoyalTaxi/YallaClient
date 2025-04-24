package uz.yalla.client.feature.notification.notifications.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.cash.paging.cachedIn
import uz.yalla.client.feature.domain.usecase.GetNotificationsUseCase

internal class NotificationsViewModel(
    private val getNotificationsUseCase: GetNotificationsUseCase
) : ViewModel() {

    val notifications = getNotificationsUseCase().cachedIn(viewModelScope)
}