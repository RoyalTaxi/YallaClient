package uz.yalla.client.core.common.di

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import uz.yalla.client.core.common.sheet.search_address.DualAddressViewModel
import uz.yalla.client.core.common.sheet.search_address.SingleAddressViewModel
import uz.yalla.client.core.common.sheet.select_from_map.SelectFromMapViewModel

object Common {
    val module = module {
        viewModelOf(::SingleAddressViewModel)
        viewModelOf(::DualAddressViewModel)
        viewModelOf(::SelectFromMapViewModel)
    }

    val modules = listOf(
        module,
        Maps.module
    )
}
