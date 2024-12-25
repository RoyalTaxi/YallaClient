package uz.ildam.technologies.yalla.feature.profile.data.di

import org.koin.core.qualifier.named
import org.koin.dsl.module
import uz.ildam.technologies.yalla.core.data.global.Constants
import uz.ildam.technologies.yalla.feature.profile.data.repository.ProfileRepositoryImpl
import uz.ildam.technologies.yalla.feature.profile.data.service.ProfileService
import uz.ildam.technologies.yalla.feature.profile.domain.repository.ProfileRepository
import uz.ildam.technologies.yalla.feature.profile.domain.usecase.GetMeUseCase
import uz.ildam.technologies.yalla.feature.profile.domain.usecase.UpdateMeUseCase

object Profile {
    private val serviceModule = module {
        single { ProfileService(get(named(Constants.API_1))) }
    }

    private val repositoryModule = module {
        single<ProfileRepository> { ProfileRepositoryImpl(get()) }
    }

    private val useCaseModule = module {
        single { GetMeUseCase(get()) }
        single { UpdateMeUseCase(get()) }
    }

    val modules = listOf(serviceModule, repositoryModule, useCaseModule)
}