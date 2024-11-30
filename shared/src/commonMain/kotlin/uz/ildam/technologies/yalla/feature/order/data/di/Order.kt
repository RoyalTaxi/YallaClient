package uz.ildam.technologies.yalla.feature.order.data.di

import org.koin.core.qualifier.named
import org.koin.dsl.module
import uz.ildam.technologies.yalla.core.data.global.Constants
import uz.ildam.technologies.yalla.feature.order.data.repository.OrderRepositoryImpl
import uz.ildam.technologies.yalla.feature.order.data.repository.TariffRepositoryImpl
import uz.ildam.technologies.yalla.feature.order.data.service.OrderService
import uz.ildam.technologies.yalla.feature.order.data.service.TariffService
import uz.ildam.technologies.yalla.feature.order.domain.repository.OrderRepository
import uz.ildam.technologies.yalla.feature.order.domain.repository.TariffRepository
import uz.ildam.technologies.yalla.feature.order.domain.usecase.order.OrderTaxiUseCase
import uz.ildam.technologies.yalla.feature.order.domain.usecase.tariff.GetTariffsUseCase
import uz.ildam.technologies.yalla.feature.order.domain.usecase.tariff.GetTimeOutUseCase

object Order {

    private val serviceModule = module {
        single { TariffService(get(named(Constants.API_2))) }
        single { OrderService(get(named(Constants.API_2))) }
    }

    private val repositoryModule = module {
        single<TariffRepository> { TariffRepositoryImpl(get()) }
        single<OrderRepository> { OrderRepositoryImpl(get()) }
    }

    private val useCaseModule = module {
        single { GetTariffsUseCase(get()) }
        single { GetTimeOutUseCase(get()) }
        single { OrderTaxiUseCase(get()) }
    }

    val modules = listOf(serviceModule, repositoryModule, useCaseModule)
}