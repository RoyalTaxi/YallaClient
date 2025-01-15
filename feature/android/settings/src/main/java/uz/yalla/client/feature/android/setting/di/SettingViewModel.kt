package uz.yalla.client.feature.android.setting.di

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import uz.yalla.client.feature.android.setting.settings.model.SettingsViewModel

object SettingViewModel {
    var module = module {
        viewModelOf(::SettingsViewModel)
    }
}