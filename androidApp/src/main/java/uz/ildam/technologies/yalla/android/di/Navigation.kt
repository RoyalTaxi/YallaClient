package uz.ildam.technologies.yalla.android.di

import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import uz.ildam.technologies.yalla.android.ui.screens.credentials.CredentialsViewModel
import uz.ildam.technologies.yalla.android.ui.screens.language.LanguageViewModel
import uz.ildam.technologies.yalla.android.ui.screens.login.LoginViewModel
import uz.ildam.technologies.yalla.android.ui.screens.map.MapViewModel
import uz.ildam.technologies.yalla.android.ui.screens.verification.VerificationViewModel

object Navigation {

    private val viewModelModule = module {
        viewModel { LanguageViewModel() }
        viewModel { LoginViewModel(get()) }
        viewModel { VerificationViewModel(get(), get()) }
        viewModel { CredentialsViewModel(get()) }
        viewModel { MapViewModel(get(), get(), get()) }
    }

    val modules = listOf(viewModelModule)
}