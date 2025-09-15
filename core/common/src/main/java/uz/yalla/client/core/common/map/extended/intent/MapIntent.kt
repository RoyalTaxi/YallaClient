package uz.yalla.client.core.common.map.extended.intent

import androidx.compose.foundation.layout.PaddingValues
import uz.yalla.client.core.domain.model.Executor
import uz.yalla.client.core.domain.model.MapPoint
import uz.yalla.client.core.domain.model.OrderStatus

sealed interface MapIntent {
    data object MapReady : MapIntent
    data class SetMarkerState(val markerState: MarkerState) : MapIntent
    data class SetMapPadding(val padding: Int) : MapIntent
    data class SetViewPadding(val padding: PaddingValues) : MapIntent
    data class SetDriver(val driver: Executor?) : MapIntent
    data class SetDrivers(val drivers: List<Executor>) : MapIntent
    data class SetLocations(val locations: List<MapPoint>) : MapIntent
    data class SetRoute(val route: List<MapPoint>) : MapIntent
    data class SetCarArrivesInMinutes(val minutes: Int?) : MapIntent
    data class SetOrderEndsInMinutes(val minutes: Int?) : MapIntent
    data class SetOrderStatus(val status: OrderStatus?) : MapIntent
    data object MoveToFirstLocation : MapIntent
    data object AnimateToFirstLocation : MapIntent
    data object MoveToMyLocation : MapIntent
    data object AnimateToMyLocation : MapIntent
    data object MoveToRoute : MapIntent
    data object AnimateToRoute : MapIntent
    data object ZoomOut : MapIntent
}