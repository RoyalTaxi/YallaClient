package uz.yalla.client.feature.history.history_details.model

import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChangedBy
import uz.yalla.client.core.common.map.static.intent.StaticMapIntent
import uz.yalla.client.core.common.map.static.model.onIntent

suspend fun HistoryDetailsViewModel.observeLocations() {
    container.stateFlow
        .distinctUntilChangedBy { it.locations }
        .collectLatest {
            staticMapViewModel.onIntent(StaticMapIntent.SetLocations(it.locations))
        }
}

suspend fun HistoryDetailsViewModel.observeRoute() {
    container.stateFlow
        .distinctUntilChangedBy { it.route }
        .collectLatest {
            staticMapViewModel.onIntent(StaticMapIntent.SetRoute(it.route))
        }
}