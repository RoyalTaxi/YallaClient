package uz.yalla.client.feature.android.intro.di

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import uz.yalla.client.feature.android.intro.language.model.LanguageViewModel

object IntroViewModel {
    val module = module {
        viewModelOf(::LanguageViewModel)
    }
}