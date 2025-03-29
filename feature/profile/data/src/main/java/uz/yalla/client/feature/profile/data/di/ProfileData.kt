package uz.yalla.client.feature.profile.data.di

import org.koin.core.qualifier.named
import org.koin.dsl.module
import uz.yalla.client.core.data.network.NetworkConstants
import uz.yalla.client.feature.profile.data.repository.LogoutRepositoryImpl
import uz.yalla.client.feature.profile.data.repository.ProfileRepositoryImpl
import uz.yalla.client.feature.profile.domain.repository.LogoutRepository
import uz.yalla.client.feature.profile.domain.repository.ProfileRepository
import uz.yalla.client.feature.profile.domain.usecase.GetMeUseCase
import uz.yalla.client.feature.profile.domain.usecase.LogoutUseCase
import uz.yalla.client.feature.profile.domain.usecase.UpdateAvatarUseCase
import uz.yalla.client.feature.profile.domain.usecase.UpdateMeUseCase
import uz.yalla.client.service.auth.service.LogoutService
import uz.yalla.client.service.profile.service.ProfileService

object ProfileData {
    private val serviceModule = module {
        single { ProfileService(get(named(NetworkConstants.API_1))) }
        single { LogoutService(get(named(NetworkConstants.API_1))) }
    }

    private val repositoryModule = module {
        single<ProfileRepository> { ProfileRepositoryImpl(get()) }
        single<LogoutRepository> { LogoutRepositoryImpl(get()) }
    }

    private val useCaseModule = module {
        single { GetMeUseCase(get()) }
        single { UpdateMeUseCase(get()) }
        single { UpdateAvatarUseCase(get()) }
        single { LogoutUseCase(get()) }
    }

    val modules = listOf(serviceModule, repositoryModule, useCaseModule)
}