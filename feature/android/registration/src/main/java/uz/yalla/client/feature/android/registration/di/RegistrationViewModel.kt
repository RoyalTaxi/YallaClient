package uz.yalla.client.feature.android.registration.di

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import uz.yalla.client.feature.android.registration.credentials.model.CredentialsViewModel

object RegistrationViewModel {
    val module = module {
        viewModelOf(::CredentialsViewModel)
    }
}