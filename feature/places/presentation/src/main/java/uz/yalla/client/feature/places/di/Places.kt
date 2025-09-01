package uz.yalla.client.feature.places.di

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import org.koin.core.module.dsl.viewModel
import uz.yalla.client.core.domain.model.type.PlaceType
import uz.yalla.client.feature.places.places.model.AddressesViewModel
import uz.yalla.client.feature.order.data.di.PlacesData
import uz.yalla.client.feature.places.place.model.PlaceViewModel

object Places {
    private val viewModelModule = module {
        viewModel {parameters ->
            PlaceViewModel(
                id = parameters.getOrNull<Int>(),
                type = parameters.get<PlaceType>(),
                postOnePlaceUseCase = get(),
                findOnePlaceUseCase = get(),
                updateOnePlaceUseCase = get(),
                deleteOnePlaceUseCase = get()
            )
        }
        viewModelOf(::AddressesViewModel)
    }

    val modules = listOf(
        viewModelModule,
        *PlacesData.modules.toTypedArray()
    )
}