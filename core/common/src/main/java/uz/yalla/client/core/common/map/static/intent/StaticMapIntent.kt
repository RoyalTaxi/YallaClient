package uz.yalla.client.core.common.map.static.intent

import uz.yalla.client.core.domain.model.MapPoint

sealed interface StaticMapIntent {
    data object MapReady : StaticMapIntent
    data class SetLocations(val points: List<MapPoint>) : StaticMapIntent
    data class SetRoute(val route: List<MapPoint>) : StaticMapIntent
}