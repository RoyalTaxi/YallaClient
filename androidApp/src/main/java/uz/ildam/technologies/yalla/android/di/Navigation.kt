package uz.ildam.technologies.yalla.android.di

import org.koin.dsl.module
import uz.ildam.technologies.yalla.android.ui.screens.credentials.CredentialsModel
import uz.ildam.technologies.yalla.android.ui.screens.login.LoginModel
import uz.ildam.technologies.yalla.android.ui.screens.onboarding.OnboardingModel
import uz.ildam.technologies.yalla.android.ui.screens.verification.VerificationModel

object Navigation {

    private val screenModule = module {
        factory { OnboardingModel() }
        factory { LoginModel(get()) }
        factory { VerificationModel(get(), get()) }
        factory { CredentialsModel(get()) }
    }

    val modules = listOf(screenModule)
}