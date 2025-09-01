package uz.yalla.client.feature.notification.show_notification.model

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import uz.yalla.client.core.common.viewmodel.BaseViewModel
import uz.yalla.client.core.common.viewmodel.LifeCycleAware
import uz.yalla.client.feature.domain.usecase.GetNotificationUseCase
import uz.yalla.client.feature.notification.show_notification.intent.ShowNotificationSideEffect
import uz.yalla.client.feature.notification.show_notification.intent.ShowNotificationState

internal class ShowNotificationViewModel(
    internal val id: Int,
    internal val getNotificationUseCase: GetNotificationUseCase
) : BaseViewModel(), LifeCycleAware,
    ContainerHost<ShowNotificationState, ShowNotificationSideEffect> {

    override val container: Container<ShowNotificationState, ShowNotificationSideEffect> =
        container(ShowNotificationState.INTERNAL)

    override val scope: CoroutineScope? = null

    override fun onCreate() {
        super.onCreate()
        viewModelScope.launch {
            getNotification(id)
        }
    }
}