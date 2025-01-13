package uz.yalla.client.feature.android.auth.di


import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import uz.yalla.client.feature.android.auth.login.model.LoginViewModel
import uz.yalla.client.feature.android.auth.verification.model.VerificationViewModel

object AuthViewModel {
    val module = module {
        viewModelOf(::LoginViewModel)
        viewModelOf(::VerificationViewModel)
    }
}