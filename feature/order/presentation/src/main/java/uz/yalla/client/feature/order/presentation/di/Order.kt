package uz.yalla.client.feature.order.presentation.di

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import uz.yalla.client.feature.order.presentation.main.model.MainSheetViewModel

object Order {
    val module = module {
        viewModelOf(::MainSheetViewModel)
    }
}