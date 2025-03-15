package uz.yalla.client.feature.android.setting.di

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import uz.yalla.client.feature.android.setting.settings.model.SettingsViewModel
import uz.yalla.client.feature.setting.data.di.SettingsData

object SettingViewModel {
    private var viewModelModule = module {
        viewModelOf(::SettingsViewModel)
    }

    val modules = listOf(
        viewModelModule,
        *SettingsData.modules.toTypedArray()
    )
}