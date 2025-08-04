package uz.yalla.client.feature.notification.notifications.model

import app.cash.paging.PagingData
import app.cash.paging.cachedIn
import kotlinx.coroutines.flow.Flow
import uz.yalla.client.core.common.viewmodel.BaseViewModel
import uz.yalla.client.feature.domain.model.NotificationModel
import uz.yalla.client.feature.domain.usecase.GetNotificationsUseCase

internal class NotificationsViewModel(
    private val getNotificationsUseCase: GetNotificationsUseCase
) : BaseViewModel() {

    val notifications: Flow<PagingData<NotificationModel>> = getNotificationsUseCase()
        .cachedIn(viewModelScope)
}
