package uz.ildam.technologies.yalla.feature.addresses.data.di

import org.koin.core.qualifier.named
import org.koin.dsl.module
import uz.ildam.technologies.yalla.core.data.global.Constants
import uz.ildam.technologies.yalla.feature.addresses.data.repository.AddressesRepositoryImpl
import uz.ildam.technologies.yalla.feature.addresses.data.service.AddressesApiService
import uz.ildam.technologies.yalla.feature.addresses.domain.repository.AddressesRepository
import uz.ildam.technologies.yalla.feature.addresses.domain.usecase.DeleteOneAddressUseCase
import uz.ildam.technologies.yalla.feature.addresses.domain.usecase.FindAllAddressesUseCase
import uz.ildam.technologies.yalla.feature.addresses.domain.usecase.FindOneAddressUseCase
import uz.ildam.technologies.yalla.feature.addresses.domain.usecase.PostOneAddressUseCase
import uz.ildam.technologies.yalla.feature.addresses.domain.usecase.UpdateOneAddressUseCase


object Addresses {

    private val serviceModule = module {
        single { AddressesApiService(get(named(Constants.API_1))) }
    }

    private val repositoryModule = module {
        single<AddressesRepository> { AddressesRepositoryImpl(get()) }
    }

    private val useCaseModule = module {
        single { FindAllAddressesUseCase(get()) }
        single { FindOneAddressUseCase(get()) }
        single { PostOneAddressUseCase(get()) }
        single { DeleteOneAddressUseCase(get()) }
        single { UpdateOneAddressUseCase(get()) }
    }

    val modules = listOf(serviceModule, repositoryModule, useCaseModule)
}