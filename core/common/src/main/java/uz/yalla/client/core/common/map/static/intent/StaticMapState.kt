package uz.yalla.client.core.common.map.static.intent

import uz.yalla.client.core.domain.model.MapPoint

data class StaticMapState(
    val mapPadding: Int, // content padding of map
    val isMapReady: Boolean,
    val locations: List<MapPoint>,
    val route: List<MapPoint>
) {
    companion object {
        val INITIAL = StaticMapState(
            mapPadding = 110,
            isMapReady = false,
            locations = emptyList(),
            route = emptyList()
        )
    }
}