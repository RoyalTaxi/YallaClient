package uz.yalla.client.feature.map.data.di

import org.koin.core.qualifier.named
import org.koin.dsl.module
import uz.yalla.client.core.data.network.NetworkConstants
import uz.yalla.client.feature.map.data.repository.MapRepositoryImpl
import uz.yalla.client.feature.map.domain.repository.MapRepository
import uz.yalla.client.feature.map.domain.usecase.GetAddressNameUseCase
import uz.yalla.client.feature.map.domain.usecase.GetPolygonUseCase
import uz.yalla.client.feature.map.domain.usecase.GetRoutingUseCase
import uz.yalla.client.feature.map.domain.usecase.SearchAddressUseCase
import uz.yalla.client.service.map.service.MapService

object MapData {

    private val serviceModule = module {
        single { MapService(get(named(NetworkConstants.API_2))) }
    }

    private val repositoryModule = module {
        single<MapRepository> { MapRepositoryImpl(get()) }
    }

    private val useCaseModule = module {
        single { GetPolygonUseCase(get()) }
        single { GetAddressNameUseCase(get()) }
        single { SearchAddressUseCase(get()) }
        single { GetRoutingUseCase(get()) }
        single { SearchAddressUseCase(get()) }
    }

    val modules = listOf(serviceModule, repositoryModule, useCaseModule)
}