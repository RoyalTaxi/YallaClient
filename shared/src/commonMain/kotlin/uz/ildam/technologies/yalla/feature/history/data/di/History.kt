package uz.ildam.technologies.yalla.feature.history.data.di

import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.module
import uz.ildam.technologies.yalla.core.data.global.Constants
import uz.ildam.technologies.yalla.feature.history.data.repository.OrderHistoryRepositoryImpl
import uz.ildam.technologies.yalla.feature.history.data.service.OrdersHistoryService
import uz.ildam.technologies.yalla.feature.history.domain.repository.OrderHistoryRepository
import uz.ildam.technologies.yalla.feature.history.domain.usecase.GetOrderHistoryUseCase
import uz.ildam.technologies.yalla.feature.history.domain.usecase.GetOrdersHistoryUseCase

object History {
    private val serviceModule = module {
        single { OrdersHistoryService(get(named(Constants.API_2))) }
    }

    private val repositoryModule = module {
        singleOf(::OrderHistoryRepositoryImpl) { bind<OrderHistoryRepository>() }
    }

    private val useCaseModule = module {
        singleOf(::GetOrdersHistoryUseCase)
        singleOf(::GetOrderHistoryUseCase)

    }

    val modules = listOf(serviceModule, repositoryModule, useCaseModule)
}