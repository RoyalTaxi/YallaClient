package uz.yalla.client.feature.android.auth.login.di

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import uz.yalla.client.feature.android.auth.login.model.LoginViewModel

object LoginViewModel {
    val module = module {
        viewModelOf(::LoginViewModel)
    }
}