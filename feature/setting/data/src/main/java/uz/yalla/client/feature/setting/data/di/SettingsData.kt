package uz.yalla.client.feature.setting.data.di

import org.koin.core.qualifier.named
import org.koin.dsl.module
import uz.yalla.client.core.data.network.NetworkConstants
import uz.yalla.client.feature.setting.data.repository.ConfigRepositoryImpl
import uz.yalla.client.feature.setting.domain.repository.ConfigRepository
import uz.yalla.client.feature.setting.domain.usecase.GetConfigUseCase
import uz.yalla.client.feature.setting.domain.usecase.RefreshFCMTokenUseCase
import uz.yalla.client.service.setting.service.ConfigService

object SettingsData {
    private val serviceModule = module {
        single {
            ConfigService(
                ktorPhp = get(named(NetworkConstants.API_1)),
                ktorGo = get(named(NetworkConstants.API_2))
            )
        }
    }

    private val repositoryModule = module {
        single<ConfigRepository> { ConfigRepositoryImpl(get(), get()) }
    }

    private val useCaseModule = module {
        single { GetConfigUseCase(get()) }
        single { RefreshFCMTokenUseCase(get(), get()) }
    }

    val modules = listOf(serviceModule, repositoryModule, useCaseModule)
}