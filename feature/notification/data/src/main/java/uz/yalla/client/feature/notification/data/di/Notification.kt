package uz.yalla.client.feature.notification.data.di

import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.module
import uz.yalla.client.core.data.network.NetworkConstants
import uz.yalla.client.feature.domain.repository.NotificationRepository
import uz.yalla.client.feature.domain.usecase.GetNotificationUseCase
import uz.yalla.client.feature.domain.usecase.GetNotificationsCountUseCase
import uz.yalla.client.feature.domain.usecase.GetNotificationsUseCase
import uz.yalla.client.feature.notification.data.repository.NotificationRepositoryImpl
import uz.yalla.client.service.notification.service.NotificationsApiService

object Notification {
    private val serviceModel = module {
        single { NotificationsApiService(get(named(NetworkConstants.API_1))) }
    }

    private val repositoryModule = module {
        singleOf(::NotificationRepositoryImpl) { bind<NotificationRepository>() }
    }

    private val useCaseModule = module {
        single { GetNotificationUseCase(get()) }
        single { GetNotificationsUseCase(get()) }
        single { GetNotificationsCountUseCase(get()) }
    }

    val modules = listOf(serviceModel, repositoryModule, useCaseModule)
}