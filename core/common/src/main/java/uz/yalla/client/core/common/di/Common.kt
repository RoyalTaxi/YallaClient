package uz.yalla.client.core.common.di

import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import uz.yalla.client.core.common.sheet.search_address.DualAddressViewModel
import uz.yalla.client.core.common.sheet.search_address.SingleAddressViewModel
import uz.yalla.client.core.common.sheet.select_from_map.SelectFromMapViewModel
import uz.yalla.client.core.common.new_map.core.MapViewModel

object Common {
    val module = module {
        viewModelOf(::SingleAddressViewModel)
        viewModelOf(::DualAddressViewModel)
        viewModelOf(::SelectFromMapViewModel)
        singleOf(::MapViewModel)
    }

    val modules = listOf(
        module
    )
}
