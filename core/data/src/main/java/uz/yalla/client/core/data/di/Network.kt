package uz.yalla.client.core.data.di

import io.ktor.client.HttpClient
import org.koin.core.qualifier.named
import org.koin.dsl.module
import uz.yalla.client.core.data.network.NetworkConstants
import uz.yalla.client.core.data.network.provideNetworkClient

object Network {
    val module = module {
        single<HttpClient>(named(NetworkConstants.API_1)) { provideNetworkClient(NetworkConstants.BASE_URL) }
        single<HttpClient>(named(NetworkConstants.API_2)) { provideNetworkClient(NetworkConstants.BASE_URL_2) }
    }
}