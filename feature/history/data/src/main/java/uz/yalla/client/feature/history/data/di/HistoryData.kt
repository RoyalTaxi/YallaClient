package uz.yalla.client.feature.history.data.di

import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.module
import uz.yalla.client.core.data.network.NetworkConstants
import uz.yalla.client.feature.domain.repository.OrderHistoryRepository
import uz.yalla.client.feature.domain.usecase.GetOrderHistoryUseCase
import uz.yalla.client.feature.domain.usecase.GetOrdersHistoryUseCase
import uz.yalla.client.feature.history.data.repository.OrderHistoryRepositoryImpl
import uz.yalla.client.service.history.service.OrdersHistoryApiService

object HistoryData {
    private val serviceModule = module {
        single { OrdersHistoryApiService(get(named(NetworkConstants.API_2))) }
    }

    private val repositoryModule = module {
        singleOf(::OrderHistoryRepositoryImpl) { bind<OrderHistoryRepository>() }
    }

    private val useCaseModule = module {
        single { GetOrdersHistoryUseCase(get()) }
        single { GetOrderHistoryUseCase(get()) }
    }

    val modules = listOf(serviceModule, repositoryModule, useCaseModule)
}