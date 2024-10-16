package uz.ildam.technologies.yalla.feature.auth.data.di

import org.koin.dsl.module
import uz.ildam.technologies.yalla.feature.auth.data.repository.AuthRepositoryImpl
import uz.ildam.technologies.yalla.feature.auth.data.repository.RegisterRepositoryImpl
import uz.ildam.technologies.yalla.feature.auth.data.service.AuthApiService
import uz.ildam.technologies.yalla.feature.auth.data.service.RegisterApiService
import uz.ildam.technologies.yalla.feature.auth.domain.repository.AuthRepository
import uz.ildam.technologies.yalla.feature.auth.domain.repository.RegisterRepository
import uz.ildam.technologies.yalla.feature.auth.domain.usecase.auth.SendAuthCodeUseCase
import uz.ildam.technologies.yalla.feature.auth.domain.usecase.auth.VerifyAuthCodeUseCase
import uz.ildam.technologies.yalla.feature.auth.domain.usecase.register.RegisterUseCase

object Auth {

    private val serviceModule = module {
        single { AuthApiService(get()) }
        single { RegisterApiService(get()) }
    }

    private val repositoryModule = module {
        single<AuthRepository> { AuthRepositoryImpl(get()) }
        single<RegisterRepository> { RegisterRepositoryImpl(get()) }
    }

    private val useCaseModule = module {
        single { SendAuthCodeUseCase(get()) }
        single { VerifyAuthCodeUseCase(get()) }
        single { RegisterUseCase(get()) }
    }

    val modules = listOf(serviceModule, repositoryModule, useCaseModule)
}