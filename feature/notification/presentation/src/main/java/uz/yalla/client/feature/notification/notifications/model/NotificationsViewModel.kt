package uz.yalla.client.feature.notification.notifications.model

import app.cash.paging.PagingData
import app.cash.paging.cachedIn
import kotlinx.coroutines.flow.Flow
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import uz.yalla.client.core.common.viewmodel.BaseViewModel
import uz.yalla.client.feature.domain.model.NotificationModel
import uz.yalla.client.feature.domain.usecase.GetNotificationsUseCase
import uz.yalla.client.feature.notification.notifications.intent.NotificationsSideEffect

internal class NotificationsViewModel(
    getNotificationsUseCase: GetNotificationsUseCase
) : BaseViewModel(), ContainerHost<Unit, NotificationsSideEffect> {

    override val container: Container<Unit, NotificationsSideEffect> =
        container(Unit)

    val notifications: Flow<PagingData<NotificationModel>> = getNotificationsUseCase()
        .cachedIn(viewModelScope)
}
