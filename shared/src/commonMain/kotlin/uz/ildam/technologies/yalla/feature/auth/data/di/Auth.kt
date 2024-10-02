package uz.ildam.technologies.yalla.feature.auth.data.di

import org.koin.dsl.module
import uz.ildam.technologies.yalla.feature.auth.data.repository.AuthRepositoryImpl
import uz.ildam.technologies.yalla.feature.auth.data.service.AuthApiService
import uz.ildam.technologies.yalla.feature.auth.domain.repository.AuthRepository
import uz.ildam.technologies.yalla.feature.auth.domain.usecase.SendAuthCodeUseCase
import uz.ildam.technologies.yalla.feature.auth.domain.usecase.ValidateAuthCodeUseCase

object Auth {

    private val serviceModule = module {
        single { AuthApiService(get()) }
    }

    private val repositoryModule = module {
        single<AuthRepository> { AuthRepositoryImpl(get()) }
    }

    private val useCaseModule = module {
        single { SendAuthCodeUseCase(get()) }
        single { ValidateAuthCodeUseCase(get()) }
    }

    val modules = listOf(serviceModule, repositoryModule, useCaseModule)
}