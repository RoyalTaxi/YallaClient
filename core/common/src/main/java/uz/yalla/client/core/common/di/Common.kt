package uz.yalla.client.core.common.di

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import uz.yalla.client.core.common.sheet.search_address.SearchByNameBottomSheetViewModel
import uz.yalla.client.core.common.sheet.select_from_map.SelectFromMapBottomSheetViewModel

object Common {
    val module = module {
        viewModelOf(::SearchByNameBottomSheetViewModel)
        viewModelOf(::SelectFromMapBottomSheetViewModel)
    }
}