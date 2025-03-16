package uz.yalla.client.feature.android.history.di

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import uz.yalla.client.feature.android.history.history.model.HistoryViewModel
import uz.yalla.client.feature.android.history.history_details.model.HistoryDetailsViewModel

object History {
    private var viewModelModule = module {
        viewModelOf(::HistoryViewModel)
        viewModelOf(::HistoryDetailsViewModel)
    }

    val modules = listOf(viewModelModule)
}