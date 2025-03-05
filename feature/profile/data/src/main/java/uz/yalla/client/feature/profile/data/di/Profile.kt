package uz.yalla.client.feature.profile.data.di

import org.koin.core.qualifier.named
import org.koin.dsl.module
import uz.ildam.technologies.yalla.feature.profile.data.repository.ProfileRepositoryImpl
import uz.ildam.technologies.yalla.feature.profile.data.service.ProfileService
import uz.ildam.technologies.yalla.feature.profile.domain.repository.ProfileRepository
import uz.ildam.technologies.yalla.feature.profile.domain.usecase.GetMeUseCase
import uz.ildam.technologies.yalla.feature.profile.domain.usecase.UpdateAvatarUseCase
import uz.ildam.technologies.yalla.feature.profile.domain.usecase.UpdateMeUseCase
import uz.yalla.client.core.data.network.NetworkConstants
import uz.yalla.client.feature.profile.data.repository.ProfileRepositoryImpl
import uz.yalla.client.feature.profile.domain.repository.ProfileRepository
import uz.yalla.client.feature.profile.domain.usecase.GetMeUseCase
import uz.yalla.client.feature.profile.domain.usecase.UpdateAvatarUseCase
import uz.yalla.client.feature.profile.domain.usecase.UpdateMeUseCase
import uz.yalla.client.service.profile.service.ProfileService

object Profile {
    private val serviceModule = module {
        single { ProfileService(get(named(NetworkConstants.API_1))) }
    }

    private val repositoryModule = module {
        single<ProfileRepository> { ProfileRepositoryImpl(get()) }
    }

    private val useCaseModule = module {
        single { GetMeUseCase(get()) }
        single { UpdateMeUseCase(get()) }
        single { UpdateAvatarUseCase(get()) }
    }

    val modules = listOf(serviceModule, repositoryModule, useCaseModule)
}