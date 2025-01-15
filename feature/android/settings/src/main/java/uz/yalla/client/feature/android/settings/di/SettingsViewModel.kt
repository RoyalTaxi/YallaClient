package uz.yalla.client.feature.android.settings.di

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import uz.yalla.client.feature.android.settings.settings.model.SettingsViewModel

object SettingsViewModel {
    var module = module {
        viewModelOf(::SettingsViewModel)
    }
}