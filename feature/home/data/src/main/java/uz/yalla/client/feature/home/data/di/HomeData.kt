package uz.yalla.client.feature.home.data.di

import org.koin.core.qualifier.named
import org.koin.dsl.module
import uz.yalla.client.core.data.network.NetworkConstants
import uz.yalla.client.feature.home.data.repository.HomeRepositoryImpl
import uz.yalla.client.feature.home.domain.repository.HomeRepository
import uz.yalla.client.feature.home.domain.usecase.GetAddressNameUseCase
import uz.yalla.client.feature.home.domain.usecase.GetPolygonUseCase
import uz.yalla.client.feature.home.domain.usecase.GetRoutingUseCase
import uz.yalla.client.feature.home.domain.usecase.GetSecondaryAddressedUseCase
import uz.yalla.client.feature.home.domain.usecase.SearchAddressUseCase
import uz.yalla.client.service.map.service.MapService

object HomeData {

    private val serviceModule = module {
        single { MapService(get(named(NetworkConstants.API_2))) }
    }

    private val repositoryModule = module {
        single<HomeRepository> { HomeRepositoryImpl(get()) }
    }

    private val useCaseModule = module {
        single { GetPolygonUseCase(get()) }
        single { GetAddressNameUseCase(get()) }
        single { SearchAddressUseCase(get()) }
        single { GetRoutingUseCase(get()) }
        single { SearchAddressUseCase(get()) }
        single { GetSecondaryAddressedUseCase(get()) }
    }

    val modules = listOf(serviceModule, repositoryModule, useCaseModule)
}