package uz.yalla.client.core.common.new_map.core

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
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
    val isMapReady: Boolean = false
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
            orderStatus = null
        )
    }
}

sealed interface MapEffect {
    data class MoveTo(
        val point: MapPoint
    ) : MapEffect

    data class MoveToWithZoom(
        val point: MapPoint,
        val zoom: Int
    ) : MapEffect

    data class AnimateTo(
        val point: MapPoint
    ) : MapEffect

    data class AnimateToWithZoom(
        val point: MapPoint,
        val zoom: Int,
    ) : MapEffect

    data class AnimateToWithZoomAndDuration(
        val point: MapPoint,
        val zoom: Int,
        val durationMs: Int
    ) : MapEffect

    data class FitBounds(
        val points: List<MapPoint>,
    ) : MapEffect

    data class AnimateFitBounds(
        val points: List<MapPoint>,
        val durationMs: Int
    ) : MapEffect

    data object ZoomOut : MapEffect
}

sealed interface MyMapIntent {
    data object MapReady : MyMapIntent

    data class SetMarkerState(val markerState: MarkerState) : MyMapIntent

    data class SetMapPadding(val padding: Int) : MyMapIntent
    data class SetViewPadding(val padding: PaddingValues) : MyMapIntent
    data class SetDriver(val driver: Executor?) : MyMapIntent
    data class SetDrivers(val drivers: List<Executor>) : MyMapIntent
    data class SetLocations(val locations: List<MapPoint>) : MyMapIntent
    data class SetRoute(val route: List<MapPoint>) : MyMapIntent
    data class SetCarArrivesInMinutes(val minutes: Int?) : MyMapIntent
    data class SetOrderEndsInMinutes(val minutes: Int?) : MyMapIntent
    data class SetOrderStatus(val status: OrderStatus?) : MyMapIntent

    data object MoveToFirstLocation : MyMapIntent
    data object AnimateToFirstLocation : MyMapIntent
    data object MoveToMyLocation : MyMapIntent
    data object AnimateToMyLocation : MyMapIntent
    data object MoveToMyRoute : MyMapIntent
    data object AnimateToMyRoute : MyMapIntent

    data object ZoomOut : MyMapIntent
}

interface Map {

    @Composable
    fun View()
}

infix operator fun PaddingValues.plus(other: PaddingValues): PaddingValues {
    return PaddingValues(
        top = calculateTopPadding()
                + other.calculateTopPadding(),
        bottom = calculateBottomPadding()
                + other.calculateBottomPadding(),
        start = calculateLeftPadding(LayoutDirection.Ltr)
                + other.calculateLeftPadding(LayoutDirection.Ltr),
        end = calculateRightPadding(LayoutDirection.Ltr)
                + other.calculateRightPadding(LayoutDirection.Ltr)
    )
}