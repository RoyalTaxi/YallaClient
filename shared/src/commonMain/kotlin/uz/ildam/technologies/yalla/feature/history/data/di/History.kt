package uz.ildam.technologies.yalla.feature.history.data.di

import org.koin.core.qualifier.named
import org.koin.dsl.module
import uz.ildam.technologies.yalla.core.data.global.Constants
import uz.ildam.technologies.yalla.feature.history.data.paging.OrdersHistoryPagingSource
import uz.ildam.technologies.yalla.feature.history.data.repository.OrderHistoryRepositoryImpl
import uz.ildam.technologies.yalla.feature.history.data.service.OrdersHistoryService
import uz.ildam.technologies.yalla.feature.history.domain.repository.OrderHistoryRepository
import uz.ildam.technologies.yalla.feature.history.domain.usecase.GetOrderHistoryUseCase

object History {
    private val serviceModule = module {
        single { OrdersHistoryService(get(named(Constants.API_2))) }
    }

    private val pagingSource = module {
        single { OrdersHistoryPagingSource(get()) }
    }

    private val repositoryModule = module {
        single<OrderHistoryRepository> { OrderHistoryRepositoryImpl(get()) }
    }

    private val useCaseModule = module {
        single { GetOrderHistoryUseCase(get()) }
    }

    val modules = listOf(serviceModule, pagingSource, repositoryModule, useCaseModule)
}