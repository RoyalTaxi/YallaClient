package uz.yalla.client.feature.map.presentation.model

import uz.yalla.client.core.common.new_map.core.MyMapIntent
import uz.yalla.client.core.domain.model.OrderStatus

fun MViewModel.refocus() {
    val state = stateFlow.value
    if (state.order?.status in OrderStatus.nonInteractive) {
        mapsViewModel.onIntent(MyMapIntent.AnimateToFirstLocation)
    } else if (state.route.isNotEmpty()) {
        mapsViewModel.onIntent(MyMapIntent.AnimateToMyRoute)
    } else if (state.order != null) {
        mapsViewModel.onIntent(MyMapIntent.AnimateToFirstLocation)
    } else {
        mapsViewModel.onIntent(MyMapIntent.AnimateToMyLocation)
    }
}