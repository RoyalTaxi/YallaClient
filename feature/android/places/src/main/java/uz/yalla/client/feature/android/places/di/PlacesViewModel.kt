package uz.yalla.client.feature.android.places.di

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import uz.yalla.client.feature.android.places.address.model.AddressViewModel
import uz.yalla.client.feature.android.places.addresses.model.AddressesViewModel

object PlacesViewModel {
    var module = module {
        viewModelOf(::AddressViewModel)
        viewModelOf(::AddressesViewModel)
    }
}