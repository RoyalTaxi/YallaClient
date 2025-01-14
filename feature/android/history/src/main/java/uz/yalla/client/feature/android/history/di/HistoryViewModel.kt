package uz.yalla.client.feature.android.history.di

import androidx.lifecycle.viewmodel.compose.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import uz.yalla.client.feature.android.history.history.model.HistoryViewModel
import uz.yalla.client.feature.android.history.history_details.model.HistoryDetailsViewModel

object HistoryViewModel {
    var module = module {
        viewModelOf(::HistoryViewModel)
        viewModelOf(::HistoryDetailsViewModel)
    }
}