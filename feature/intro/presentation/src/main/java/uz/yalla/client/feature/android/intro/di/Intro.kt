package uz.yalla.client.feature.android.intro.di

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import uz.yalla.client.feature.android.intro.language.model.LanguageViewModel

object Intro {
    private val viewModelModule = module {
        viewModelOf(::LanguageViewModel)
    }

    val modules = listOf(viewModelModule)
}