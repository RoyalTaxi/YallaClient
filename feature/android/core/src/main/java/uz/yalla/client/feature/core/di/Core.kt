package uz.yalla.client.feature.core.di

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import uz.yalla.client.feature.core.sheets.search_address.SearchByNameBottomSheetViewModel
import uz.yalla.client.feature.core.sheets.select_from_map.SelectFromMapBottomSheetViewModel

object Core {
    val module = module {
        viewModelOf(::SearchByNameBottomSheetViewModel)
        viewModelOf(::SelectFromMapBottomSheetViewModel)
    }
}