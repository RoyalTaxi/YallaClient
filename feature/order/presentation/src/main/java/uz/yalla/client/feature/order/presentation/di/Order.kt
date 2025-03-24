package uz.yalla.client.feature.order.presentation.di

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import uz.yalla.client.feature.order.data.di.OrderData
import uz.yalla.client.feature.order.presentation.cancel.CancelReasonViewModel
import uz.yalla.client.feature.order.presentation.main.model.MainSheetViewModel
import uz.yalla.client.feature.order.presentation.search.model.SearchCarSheetViewModel

object Order {
    private val viewModelModule = module {
        viewModelOf(::MainSheetViewModel)
        viewModelOf(::SearchCarSheetViewModel)
        viewModelOf(::CancelReasonViewModel)
    }

    val modules = listOf(
        viewModelModule,
        *OrderData.modules.toTypedArray()
    )
}