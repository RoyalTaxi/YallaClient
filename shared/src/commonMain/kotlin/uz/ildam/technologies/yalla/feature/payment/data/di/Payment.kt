package uz.ildam.technologies.yalla.feature.payment.data.di

import org.koin.core.qualifier.named
import org.koin.dsl.module
import uz.ildam.technologies.yalla.core.data.global.Constants
import uz.ildam.technologies.yalla.feature.payment.data.repository.AddCardRepositoryImpl
import uz.ildam.technologies.yalla.feature.payment.data.repository.CardListRepositoryImpl
import uz.ildam.technologies.yalla.feature.payment.data.service.AddCardApiService
import uz.ildam.technologies.yalla.feature.payment.data.service.CardListApiService
import uz.ildam.technologies.yalla.feature.payment.domain.repository.AddCardRepository
import uz.ildam.technologies.yalla.feature.payment.domain.repository.CardListRepository
import uz.ildam.technologies.yalla.feature.payment.domain.usecase.AddCardUseCase
import uz.ildam.technologies.yalla.feature.payment.domain.usecase.GetCardListUseCase
import uz.ildam.technologies.yalla.feature.payment.domain.usecase.VerifyCardUseCase

object Payment {
    private val serviceModule = module {
        single { AddCardApiService(get(named(Constants.API_2))) }
        single { CardListApiService(get(named(Constants.API_2))) }
    }

    private val repositoryModule = module {
        single<AddCardRepository> { AddCardRepositoryImpl(get()) }
        single<CardListRepository> { CardListRepositoryImpl(get()) }
    }

    private val useCaseModule = module {
        single { AddCardUseCase(get()) }
        single { VerifyCardUseCase(get()) }
        single { GetCardListUseCase(get()) }
    }

    val modules = listOf(serviceModule, repositoryModule, useCaseModule)
}