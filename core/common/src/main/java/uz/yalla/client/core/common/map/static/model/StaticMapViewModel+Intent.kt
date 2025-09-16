package uz.yalla.client.core.common.map.static.model

import uz.yalla.client.core.common.map.static.intent.StaticMapEffect
import uz.yalla.client.core.common.map.static.intent.StaticMapIntent

fun StaticMapViewModel.onIntent(intent: StaticMapIntent) = intent {
    when (intent) {
        StaticMapIntent.MapReady -> reduce { state.copy(isMapReady = true) }
        is StaticMapIntent.SetLocations -> {
            reduce { state.copy(locations = intent.points) }
            if (intent.points.isEmpty()) postSideEffect(StaticMapEffect.MoveToFirstLocation)
            else postSideEffect(StaticMapEffect.MoveToFitBounds)
        }

        is StaticMapIntent.SetRoute -> {
            reduce { state.copy(route = intent.route) }
            if (intent.route.isEmpty()) postSideEffect(StaticMapEffect.MoveToFirstLocation)
            else postSideEffect(StaticMapEffect.MoveToFitBounds)
        }
    }
}