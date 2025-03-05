package uz.yalla.client.feature.android.info.di

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import uz.yalla.client.feature.android.info.about_app.model.AboutAppViewModel

object InfoViewModel {
    var module = module {
        viewModelOf(::AboutAppViewModel)
    }
}