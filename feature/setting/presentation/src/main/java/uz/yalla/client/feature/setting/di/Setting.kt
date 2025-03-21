package uz.yalla.client.feature.setting.di

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import uz.yalla.client.feature.setting.model.SettingsViewModel
import uz.yalla.client.feature.setting.data.di.SettingsData

object Setting {
    private var viewModelModule = module {
        viewModelOf(::SettingsViewModel)
    }

    val modules = listOf(
        viewModelModule,
        *SettingsData.modules.toTypedArray()
    )
}