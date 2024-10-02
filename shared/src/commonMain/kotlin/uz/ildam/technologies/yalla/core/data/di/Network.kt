package uz.ildam.technologies.yalla.core.data.di

import org.koin.dsl.module
import uz.ildam.technologies.yalla.core.data.global.provideHttpClient

object Network {
    val module = module {
        single { provideHttpClient() }
    }
}