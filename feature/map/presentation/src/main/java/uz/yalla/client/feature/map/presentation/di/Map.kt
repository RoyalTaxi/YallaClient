package uz.yalla.client.feature.map.presentation.di

import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import uz.yalla.client.core.common.maps.MapsViewModel
import uz.yalla.client.feature.map.data.di.MapData
import uz.yalla.client.feature.map.presentation.model.MapViewModel
import uz.yalla.client.feature.map.presentation.new_version.model.MViewModel

object Map {
    private val viewModelModule = module {
        viewModelOf(::MapViewModel)
        viewModel { parameters ->
            MViewModel(
                mapsViewModel = parameters.get<MapsViewModel>(),
                prefs = get(),
                staticPrefs = get(),
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
