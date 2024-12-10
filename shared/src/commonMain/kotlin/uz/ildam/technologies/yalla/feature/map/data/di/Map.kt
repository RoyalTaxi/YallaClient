package uz.ildam.technologies.yalla.feature.map.data.di

import org.koin.core.qualifier.named
import org.koin.dsl.module
import uz.ildam.technologies.yalla.core.data.global.Constants
import uz.ildam.technologies.yalla.feature.map.data.repository.MapRepositoryImpl
import uz.ildam.technologies.yalla.feature.map.data.service.MapService
import uz.ildam.technologies.yalla.feature.map.domain.repository.MapRepository
import uz.ildam.technologies.yalla.feature.map.domain.usecase.map.GetAddressNameUseCase
import uz.ildam.technologies.yalla.feature.map.domain.usecase.map.GetPolygonUseCase
import uz.ildam.technologies.yalla.feature.map.domain.usecase.map.SearchForAddressUseCase

object Map {

    private val serviceModule = module {
        single { MapService(get(named(Constants.API_2))) }
    }

    private val repositoryModule = module {
        single<MapRepository> { MapRepositoryImpl(get()) }
    }

    private val useCaseModule = module {
        single { GetPolygonUseCase(get()) }
        single { GetAddressNameUseCase(get()) }
        single { SearchForAddressUseCase(get()) }
    }

    val modules = listOf(serviceModule, repositoryModule, useCaseModule)
}