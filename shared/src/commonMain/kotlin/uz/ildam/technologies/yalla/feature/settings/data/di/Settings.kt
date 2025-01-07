package uz.ildam.technologies.yalla.feature.settings.data.di

import org.koin.core.qualifier.named
import org.koin.dsl.module
import uz.ildam.technologies.yalla.core.data.global.Constants
import uz.ildam.technologies.yalla.feature.settings.data.repository.SettingRepositoryImpl
import uz.ildam.technologies.yalla.feature.settings.data.service.SettingsService
import uz.ildam.technologies.yalla.feature.settings.domain.repository.SettingRepository
import uz.ildam.technologies.yalla.feature.settings.domain.usecase.GetConfigUseCase

object Settings {
    private val serviceModule = module {
        single { SettingsService(get(named(Constants.API_1))) }
    }

    private val repositoryModule = module {
        single<SettingRepository> { SettingRepositoryImpl(get()) }
    }

    private val useCaseModule = module {
        single { GetConfigUseCase(get()) }
    }

    val modules = listOf(serviceModule, repositoryModule, useCaseModule)
}