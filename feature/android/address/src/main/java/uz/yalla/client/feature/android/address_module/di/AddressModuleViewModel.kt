package uz.yalla.client.feature.android.address_module.di

import androidx.lifecycle.viewmodel.compose.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import uz.yalla.client.feature.android.address_module.address.model.AddressViewModel
import uz.yalla.client.feature.android.address_module.addresses.model.AddressesViewModel

object AddressModuleViewModel {
    var module = module {
        viewModelOf(::AddressViewModel)
        viewModelOf(::AddressesViewModel)
    }
}