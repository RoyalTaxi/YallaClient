package uz.yalla.client.core.common.map.extended.intent

import androidx.compose.foundation.layout.PaddingValues
import uz.yalla.client.core.domain.model.Executor
import uz.yalla.client.core.domain.model.MapPoint
import uz.yalla.client.core.domain.model.OrderStatus

data class MarkerState(
    val point: MapPoint,
    val isMoving: Boolean,
    val isByUser: Boolean,
) {
    companion object {
        val INITIAL = MarkerState(
            point = MapPoint.Zero,
            isMoving = false,
            isByUser = false,
        )
    }
}

data class MapState(
    val markerState: MarkerState,
    val viewPadding: PaddingValues, // modifier padding of map
    val mapPadding: Int, // content padding of map
    val driver: Executor?,
    val drivers: List<Executor>,
    val locations: List<MapPoint>,
    val route: List<MapPoint>,
    val carArrivesInMinutes: Int?,
    val orderEndsInMinutes: Int?,
    val orderStatus: OrderStatus?,
    val isMapReady: Boolean
) {
    companion object {
        val INITIAL = MapState(
            markerState = MarkerState.INITIAL,
            viewPadding = PaddingValues(),
            mapPadding = 110,
            driver = null,
            drivers = emptyList(),
            locations = emptyList(),
            route = emptyList(),
            carArrivesInMinutes = null,
            orderEndsInMinutes = null,
            orderStatus = null,
            isMapReady = false
        )
    }
}