package uz.yalla.client.feature.map.presentation.di

import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import uz.yalla.client.core.common.new_map.core.MapViewModel
import uz.yalla.client.feature.map.data.di.MapData
import uz.yalla.client.feature.map.presentation.model.MViewModel

object Map {
    private val viewModelModule = module {
        viewModel {
            MViewModel(
                mapsViewModel = get<MapViewModel>(),
                prefs = get(),
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
        *MapData.modules.toTypedArray()
    )
}
