package uz.yalla.client.feature.order.data.di

import org.koin.core.qualifier.named
import org.koin.dsl.module
import uz.yalla.client.core.data.network.NetworkConstants
import uz.yalla.client.feature.order.data.repository.PlacesRepositoryImpl
import uz.yalla.client.feature.order.domain.repository.PlacesRepository
import uz.yalla.client.feature.order.domain.usecase.DeleteOnePlaceUseCase
import uz.yalla.client.feature.order.domain.usecase.FindAllPlacesUseCase
import uz.yalla.client.feature.order.domain.usecase.FindOnePlaceUseCase
import uz.yalla.client.feature.order.domain.usecase.PostOnePlaceUseCase
import uz.yalla.client.feature.order.domain.usecase.UpdateOnePlaceUseCase
import uz.yalla.client.service.places.service.PlacesApiService


object PlacesData {

    private val serviceModule = module {
        single { PlacesApiService(get(named(NetworkConstants.API_1))) }
    }

    private val repositoryModule = module {
        single<PlacesRepository> { PlacesRepositoryImpl(get()) }
    }

    private val useCaseModule = module {
        single { FindAllPlacesUseCase(get()) }
        single { FindOnePlaceUseCase(get()) }
        single { PostOnePlaceUseCase(get()) }
        single { DeleteOnePlaceUseCase(get()) }
        single { UpdateOnePlaceUseCase(get()) }
    }

    val modules = listOf(serviceModule, repositoryModule, useCaseModule)
}