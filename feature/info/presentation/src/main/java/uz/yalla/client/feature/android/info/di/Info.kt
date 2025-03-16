package uz.yalla.client.feature.android.info.di

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import uz.yalla.client.feature.android.info.about_app.model.AboutAppViewModel

object Info {
    private var viewModelModule = module {
        viewModelOf(::AboutAppViewModel)
    }

    val modules = listOf(viewModelModule)
}