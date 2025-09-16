package uz.yalla.client.feature.home.presentation.di

import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import uz.yalla.client.feature.home.data.di.HomeData
import uz.yalla.client.feature.home.presentation.model.HomeViewModel

object Home {
    private val viewModelModule = module {
        viewModel {
            HomeViewModel(
                mapViewModel = get(),
                prefs = get(),
                staticPreferences = get(),
                getMeUseCase = get(),
                getAddressNameUseCase = get(),
                getShowOrderUseCase = get(),
                getRoutingUseCase = get(),
                getActiveOrdersUseCase = get(),
                getNotificationsCountUseCase = get(),
                getSettingUseCase = get()
            )
        }
    }

    val modules = listOf(
        viewModelModule,
        *HomeData.modules.toTypedArray()
    )
}
