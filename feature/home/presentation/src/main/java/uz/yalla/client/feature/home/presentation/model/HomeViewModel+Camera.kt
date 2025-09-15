package uz.yalla.client.feature.home.presentation.model

import uz.yalla.client.core.common.map.extended.intent.MapIntent.*
import uz.yalla.client.core.domain.model.OrderStatus
import kotlin.collections.contains

fun HomeViewModel.refocus() {
    val state = container.stateFlow.value
    when {
        state.order?.status in OrderStatus.nonInteractive -> {
            mapViewModel.onIntent(AnimateToFirstLocation)
        }

        state.route.isNotEmpty() -> {
            mapViewModel.onIntent(AnimateToRoute)
        }

        state.order != null -> {
            mapViewModel.onIntent(AnimateToFirstLocation)
        }

        state.oldServiceAvailability != false && state.newServiceAvailability != false -> {
            mapViewModel.onIntent(AnimateToMyLocation)
        }
    }
}