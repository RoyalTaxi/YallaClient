package uz.yalla.client.core.data.di

import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import uz.yalla.client.core.data.local.AppPreferencesImpl
import uz.yalla.client.core.domain.local.AppPreferences

object Local {
    val module = module {
        single<AppPreferences> { AppPreferencesImpl(androidContext()) }
    }
}