package uz.yalla.client.feature.notification.di

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import uz.yalla.client.feature.notification.model.NotificationViewModel

object Notifications {
    private var viewModelModule = module {
        viewModelOf(::NotificationViewModel)
    }

    val modules = listOf(viewModelModule)
}