package uz.ildam.technologies.yalla.android.di

import org.koin.dsl.module
import uz.ildam.technologies.yalla.android.ui.screens.credentials.CredentialsModel
import uz.ildam.technologies.yalla.android.ui.screens.onboarding.OnboardingModel

object Navigation {

    val module = module {
        factory { OnboardingModel() }
        factory { CredentialsModel(get()) }
    }
}