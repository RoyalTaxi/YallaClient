package uz.yalla.client.feature.history.di

import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import uz.yalla.client.feature.history.data.di.HistoryData
import uz.yalla.client.feature.history.history.model.HistoryViewModel
import uz.yalla.client.feature.history.history_details.model.HistoryDetailsViewModel

object History {
    private var viewModelModule = module {
        viewModelOf(::HistoryViewModel)
        viewModel{parameters ->
            HistoryDetailsViewModel(
                orderId = parameters.get<Int>(),
                getOrderHistoryUseCase = get(),
                getTariffsUseCase = get()
            )
        }
    }

    val modules = listOf(
        viewModelModule,
        *HistoryData.modules.toTypedArray()
    )
}