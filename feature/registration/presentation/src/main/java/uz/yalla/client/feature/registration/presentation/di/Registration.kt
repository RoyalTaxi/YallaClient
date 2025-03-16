package uz.yalla.client.feature.registration.presentation.di

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import uz.yalla.client.feature.registration.presentation.model.RegistrationViewModel

object Registration {
    private val viewModelModule = module {
        viewModelOf(::RegistrationViewModel)
    }

    val modules = listOf(viewModelModule)
}