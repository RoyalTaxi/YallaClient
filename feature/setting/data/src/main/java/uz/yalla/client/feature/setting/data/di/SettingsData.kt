package uz.yalla.client.feature.setting.data.di

import org.koin.core.qualifier.named
import org.koin.dsl.module
import uz.yalla.client.core.data.network.NetworkConstants
import uz.yalla.client.feature.setting.data.repository.SettingRepositoryImpl
import uz.yalla.client.feature.setting.domain.repository.SettingRepository
import uz.yalla.client.feature.setting.domain.usecase.GetConfigUseCase
import uz.yalla.client.feature.setting.domain.usecase.SendFCMTokenUseCase
import uz.yalla.client.service.setting.service.SettingsService

object SettingsData {
    private val serviceModule = module {
        single {
            SettingsService(
                ktorPhp = get(named(NetworkConstants.API_1)),
                ktorGo = get(named(NetworkConstants.API_2))
            )
        }
    }

    private val repositoryModule = module {
        single<SettingRepository> { SettingRepositoryImpl(get()) }
    }

    private val useCaseModule = module {
        single { GetConfigUseCase(get()) }
        single { SendFCMTokenUseCase(get()) }
    }

    val modules = listOf(serviceModule, repositoryModule, useCaseModule)
}