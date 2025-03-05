package uz.yalla.client.feature.payment.data.di

import org.koin.core.qualifier.named
import org.koin.dsl.module
import uz.yalla.client.core.data.network.NetworkConstants
import uz.yalla.client.feature.payment.data.repository.AddCardRepositoryImpl
import uz.yalla.client.feature.payment.data.repository.CardListRepositoryImpl
import uz.yalla.client.feature.payment.domain.repository.AddCardRepository
import uz.yalla.client.feature.payment.domain.repository.CardListRepository
import uz.yalla.client.feature.payment.domain.usecase.AddCardUseCase
import uz.yalla.client.feature.payment.domain.usecase.GetCardListUseCase
import uz.yalla.client.feature.payment.domain.usecase.VerifyCardUseCase
import uz.yalla.client.service.payment.service.AddCardApiService
import uz.yalla.client.service.payment.service.CardListApiService

object PaymentData {
    private val serviceModule = module {
        single { AddCardApiService(get(named(NetworkConstants.API_2))) }
        single { CardListApiService(get(named(NetworkConstants.API_2))) }
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