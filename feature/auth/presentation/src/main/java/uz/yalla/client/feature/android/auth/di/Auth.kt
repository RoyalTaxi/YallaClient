package uz.yalla.client.feature.android.auth.di


import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import uz.yalla.client.feature.android.auth.login.model.LoginViewModel
import uz.yalla.client.feature.android.auth.verification.model.VerificationViewModel
import uz.yalla.client.feature.auth.data.di.AuthData

object Auth {
    private val viewModelModule = module {
        viewModelOf(::LoginViewModel)
        viewModelOf(::VerificationViewModel)
    }

    val modules = listOf(
        viewModelModule,
        *AuthData.modules.toTypedArray()
    )
}