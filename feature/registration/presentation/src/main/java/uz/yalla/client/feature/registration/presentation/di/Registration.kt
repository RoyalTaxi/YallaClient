package uz.yalla.client.feature.registration.presentation.di

import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import uz.yalla.client.feature.registration.presentation.model.RegistrationViewModel

object Registration {
    private val viewModelModule = module {
        viewModel { parameters ->
            RegistrationViewModel(
                secretKey = parameters.get<String>(),
                phoneNumber = parameters.get<String>(),
                registerUseCase = get(),
                appPreferences = get(),
                staticPreferences = get()
            )
        }
    }

    val modules = listOf(viewModelModule)
}