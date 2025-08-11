package uz.yalla.client.feature.promocode.data.di

import org.koin.core.qualifier.named
import org.koin.dsl.module
import uz.yalla.client.core.data.network.NetworkConstants
import uz.yalla.client.feature.promocode.data.repository.PromocodeRepositoryImpl
import uz.yalla.client.feature.promocode.domain.repository.PromocodeRepository
import uz.yalla.client.feature.promocode.domain.usecase.ActivatePromocodeUseCase
import uz.yalla.client.service.promocode.service.PromocodeApiService

object PromocodeData {

    private val serviceModule = module {
        single { PromocodeApiService(get(named(NetworkConstants.API_1))) }
    }

    private val repositoryModule = module {
        single<PromocodeRepository> { PromocodeRepositoryImpl(get()) }
    }

    private val useCaseModule = module {
        single { ActivatePromocodeUseCase(get()) }
    }

    val modules = listOf(serviceModule, repositoryModule, useCaseModule)
}