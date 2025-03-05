package uz.yalla.client.feature.auth.data.di

import org.koin.core.qualifier.named
import org.koin.dsl.module
import uz.yalla.client.feature.auth.data.repository.AuthRepositoryImpl
import uz.yalla.client.feature.auth.data.repository.RegisterRepositoryImpl
import uz.yalla.client.core.data.network.NetworkConstants
import uz.yalla.client.feature.auth.domain.repository.AuthRepository
import uz.yalla.client.feature.auth.domain.repository.RegisterRepository
import uz.yalla.client.feature.auth.domain.usecase.auth.SendCodeUseCase
import uz.yalla.client.feature.auth.domain.usecase.auth.VerifyCodeUseCase
import uz.yalla.client.feature.auth.domain.usecase.register.RegisterUseCase
import uz.yalla.client.service.auth.service.AuthApiService
import uz.yalla.client.service.auth.service.RegisterApiService

object AuthData {

    private val serviceModule = module {
        single { AuthApiService(get(named(NetworkConstants.API_1))) }
        single { RegisterApiService(get(named(NetworkConstants.API_1))) }
    }

    private val repositoryModule = module {
        single<AuthRepository> { AuthRepositoryImpl(get()) }
        single<RegisterRepository> { RegisterRepositoryImpl(get()) }
    }

    private val useCaseModule = module {
        single { SendCodeUseCase(get()) }
        single { VerifyCodeUseCase(get()) }
        single { RegisterUseCase(get()) }
    }

    val modules = listOf(serviceModule, repositoryModule, useCaseModule)
}