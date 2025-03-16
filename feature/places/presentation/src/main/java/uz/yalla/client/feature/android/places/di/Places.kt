package uz.yalla.client.feature.android.places.di

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import uz.yalla.client.feature.android.places.place.model.PlaceViewModel
import uz.yalla.client.feature.android.places.places.model.AddressesViewModel
import uz.yalla.client.feature.order.data.di.PlacesData

object Places {
    private val viewModelModule = module {
        viewModelOf(::PlaceViewModel)
        viewModelOf(::AddressesViewModel)
    }

    val modules = listOf(
        viewModelModule,
        *PlacesData.modules.toTypedArray()
    )
}