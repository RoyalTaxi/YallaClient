package uz.yalla.client.core.common.map.static.model

import uz.yalla.client.core.common.map.static.intent.StaticMapEffect
import uz.yalla.client.core.common.map.static.intent.StaticMapIntent

fun StaticMapViewModel.onIntent(intent: StaticMapIntent) = intent {
    when (intent) {
        StaticMapIntent.MapReady -> reduce { state.copy(isMapReady = true) }
        is StaticMapIntent.SetLocations -> {
            reduce { state.copy(locations = intent.points) }
            postSideEffect(StaticMapEffect.MoveToFitBounds)
        }

        is StaticMapIntent.SetRoute -> {
            reduce { state.copy(route = intent.route) }
            postSideEffect(StaticMapEffect.MoveToFitBounds)
        }
    }
}