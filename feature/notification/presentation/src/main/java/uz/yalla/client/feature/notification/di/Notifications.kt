package uz.yalla.client.feature.notification.di

import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import uz.yalla.client.feature.notification.data.di.Notification
import uz.yalla.client.feature.notification.notifications.model.NotificationsViewModel
import uz.yalla.client.feature.notification.show_notification.model.ShowNotificationViewModel

object Notifications {
    private var viewModelModule = module {
        viewModelOf(::NotificationsViewModel)
        viewModel { parameters ->
            ShowNotificationViewModel(
                id = parameters.get<Int>(),
                getNotificationUseCase = get()
            )
        }
    }

    val modules = listOf(
        viewModelModule,
        *Notification.modules.toTypedArray()
    )
}