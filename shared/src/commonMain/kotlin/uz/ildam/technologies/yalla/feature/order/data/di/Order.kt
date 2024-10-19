package uz.ildam.technologies.yalla.feature.order.data.di

import org.koin.core.qualifier.named
import org.koin.dsl.module
import uz.ildam.technologies.yalla.core.data.global.Constants
import uz.ildam.technologies.yalla.feature.order.data.repository.MapRepositoryImpl
import uz.ildam.technologies.yalla.feature.order.data.service.MapService
import uz.ildam.technologies.yalla.feature.order.domain.repository.MapRepository
import uz.ildam.technologies.yalla.feature.order.domain.usecase.map.GetPolygonUseCase

object Order {

    private val serviceModule = module {
        single { MapService(get(named(Constants.API_2))) }
    }

    private val repositoryModule = module {
        single<MapRepository> { MapRepositoryImpl(get()) }
    }

    private val useCaseModule = module {
        single { GetPolygonUseCase(get()) }
    }

    val modules = listOf(serviceModule, repositoryModule, useCaseModule)
}