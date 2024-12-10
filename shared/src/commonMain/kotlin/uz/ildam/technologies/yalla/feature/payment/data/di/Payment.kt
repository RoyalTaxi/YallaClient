package uz.ildam.technologies.yalla.feature.payment.data.di

import org.koin.core.qualifier.named
import org.koin.dsl.module
import uz.ildam.technologies.yalla.core.data.global.Constants
import uz.ildam.technologies.yalla.feature.auth.domain.usecase.auth.VerifyAuthCodeUseCase
import uz.ildam.technologies.yalla.feature.payment.data.repository.AddCardRepositoryImpl
import uz.ildam.technologies.yalla.feature.payment.data.service.AddCardApiService
import uz.ildam.technologies.yalla.feature.payment.domain.repository.AddCardRepository
import uz.ildam.technologies.yalla.feature.payment.domain.usecase.AddCardUseCase

object Payment {
    private val serviceModule = module {
        single { AddCardApiService(get(named(Constants.API_2))) }
    }

    private val repositoryModule = module {
        single<AddCardRepository> { AddCardRepositoryImpl(get()) }
    }

    private val useCaseModule = module {
        single { AddCardUseCase(get()) }
        single { VerifyAuthCodeUseCase(get()) }
    }

    val modules = listOf(serviceModule, repositoryModule, useCaseModule)
}