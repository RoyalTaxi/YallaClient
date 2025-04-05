package uz.yalla.client.feature.order.data.di

import org.koin.core.qualifier.named
import org.koin.dsl.module
import uz.yalla.client.core.data.network.NetworkConstants
import uz.yalla.client.feature.order.data.repository.OrderRepositoryImpl
import uz.yalla.client.feature.order.data.repository.TariffRepositoryImpl
import uz.yalla.client.feature.order.domain.repository.OrderRepository
import uz.yalla.client.feature.order.domain.repository.TariffRepository
import uz.yalla.client.feature.order.domain.usecase.order.CancelReasonUseCase
import uz.yalla.client.feature.order.domain.usecase.order.CancelRideUseCase
import uz.yalla.client.feature.order.domain.usecase.order.GetActiveOrdersUseCase
import uz.yalla.client.feature.order.domain.usecase.order.GetSettingUseCase
import uz.yalla.client.feature.order.domain.usecase.order.GetShowOrderUseCase
import uz.yalla.client.feature.order.domain.usecase.order.OrderFasterUseCase
import uz.yalla.client.feature.order.domain.usecase.order.OrderTaxiUseCase
import uz.yalla.client.feature.order.domain.usecase.order.RateTheRideUseCase
import uz.yalla.client.feature.order.domain.usecase.order.SearchCarUseCase
import uz.yalla.client.feature.order.domain.usecase.tariff.GetTariffsUseCase
import uz.yalla.client.feature.order.domain.usecase.tariff.GetTimeOutUseCase
import uz.yalla.client.service.order.service.OrderApiService
import uz.yalla.client.service.order.service.TariffApiService

object OrderData {

    private val serviceModule = module {
        single { TariffApiService(get(named(NetworkConstants.API_2))) }
        single {
            OrderApiService(
                ktorApi1 = get(named(NetworkConstants.API_1)),
                ktorApi2 = get(named(NetworkConstants.API_2))
            )
        }
    }

    private val repositoryModule = module {
        single<TariffRepository> { TariffRepositoryImpl(get()) }
        single<OrderRepository> { OrderRepositoryImpl(get()) }
    }

    private val useCaseModule = module {
        single { GetTariffsUseCase(get()) }
        single { GetTimeOutUseCase(get()) }
        single { OrderTaxiUseCase(get()) }
        single { SearchCarUseCase(get()) }
        single { GetSettingUseCase(get()) }
        single { CancelRideUseCase(get()) }
        single { CancelReasonUseCase(get()) }
        single { GetShowOrderUseCase(get()) }
        single { RateTheRideUseCase(get()) }
        single { GetActiveOrdersUseCase(get()) }
        single { OrderFasterUseCase(get()) }
    }

    val modules = listOf(serviceModule, repositoryModule, useCaseModule)
}