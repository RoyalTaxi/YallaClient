package uz.yalla.client.feature.home.presentation.model

import uz.yalla.client.core.common.map.core.intent.MapIntent.*
import uz.yalla.client.core.domain.model.OrderStatus
import kotlin.collections.contains

fun HomeViewModel.refocus() {
    val state = container.stateFlow.value
    when {
        state.order?.status in OrderStatus.nonInteractive -> {
            mapsViewModel.onIntent(AnimateToFirstLocation)
        }

        state.route.isNotEmpty() -> {
            mapsViewModel.onIntent(AnimateToRoute)
        }

        state.order != null -> {
            mapsViewModel.onIntent(AnimateToFirstLocation)
        }

        state.serviceAvailable != false -> {
            mapsViewModel.onIntent(AnimateToMyLocation)
        }
    }
}