package uz.ildam.technologies.yalla.core.data.di

import io.ktor.client.HttpClient
import org.koin.core.qualifier.named
import org.koin.dsl.module
import uz.ildam.technologies.yalla.core.data.global.Constants
import uz.ildam.technologies.yalla.core.data.global.provideHttpClientForApi1
import uz.ildam.technologies.yalla.core.data.global.provideHttpClientForApi2

object Network {
    val module = module {
        single<HttpClient>(named(Constants.API_1)) { provideHttpClientForApi1() }
        single<HttpClient>(named(Constants.API_2)) { provideHttpClientForApi2() }
    }
}