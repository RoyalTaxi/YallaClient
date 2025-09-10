package uz.yalla.client.core.common.map.core.model

import android.content.Context
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import uz.yalla.client.core.common.map.core.MapConstants
import uz.yalla.client.core.common.map.core.intent.MapEffect
import uz.yalla.client.core.common.map.core.intent.MapEffect.*
import uz.yalla.client.core.common.map.core.intent.MapState
import uz.yalla.client.core.common.map.core.intent.MarkerState
import uz.yalla.client.core.common.map.core.intent.MapIntent
import uz.yalla.client.core.common.utils.getCurrentLocation
import uz.yalla.client.core.common.viewmodel.BaseViewModel
import uz.yalla.client.core.domain.model.MapPoint

class MapViewModel(
    private val appContext: Context
) : BaseViewModel(), ContainerHost<MapState, MapEffect> {
    override val container: Container<MapState, MapEffect> = container(MapState.INITIAL)

    val markerState: Flow<MarkerState> =
        container.stateFlow
            .map { it.markerState }
            .distinctUntilChanged()

    val isMapReady: Flow<Boolean> =
        container.stateFlow
            .map { it.isMapReady }
            .distinctUntilChanged()

    fun onIntent(intent: MapIntent) = intent {
        when (intent) {
            MapIntent.MapReady -> {
                reduce { state.copy(isMapReady = true) }
            }

            is MapIntent.SetMarkerState -> {
                reduce { state.copy(markerState = intent.markerState) }
            }

            MapIntent.MoveToFirstLocation -> {
                state.locations.firstOrNull()?.let { point ->
                    postSideEffect(MoveTo(point))
                }
            }

            MapIntent.AnimateToFirstLocation -> {
                state.locations.firstOrNull()?.let { point ->
                    postSideEffect(AnimateToWithZoom(point, MapConstants.DEFAULT_ZOOM))
                }
            }

            MapIntent.MoveToMyLocation -> {
                getCurrentLocation(
                    context = appContext,
                    onLocationFetched = { latLng ->
                        viewModelScope.launch {
                            postSideEffect(
                                MoveToWithZoom(
                                    point = MapPoint(latLng.latitude, latLng.longitude),
                                    zoom = MapConstants.DEFAULT_ZOOM
                                )
                            )
                        }
                    }
                )
            }

            MapIntent.AnimateToMyLocation -> {
                getCurrentLocation(
                    context = appContext,
                    onLocationFetched = { latLng ->
                        viewModelScope.launch {
                            postSideEffect(
                                AnimateToWithZoom(
                                    point = MapPoint(latLng.latitude, latLng.longitude),
                                    zoom = MapConstants.DEFAULT_ZOOM
                                )
                            )
                        }
                    }
                )
            }

            MapIntent.MoveToRoute -> {
                postSideEffect(FitBounds(state.route))
            }

            MapIntent.AnimateToRoute -> {
                postSideEffect(
                    AnimateFitBounds(
                        points = state.route,
                        durationMs = MapConstants.ANIMATION_DURATION_MS
                    )
                )
            }

            is MapIntent.SetCarArrivesInMinutes -> {
                reduce { state.copy(carArrivesInMinutes = intent.minutes) }
            }

            is MapIntent.SetDriver -> {
                reduce { state.copy(driver = intent.driver) }
            }

            is MapIntent.SetDrivers -> {
                reduce { state.copy(drivers = intent.drivers) }
            }

            is MapIntent.SetLocations -> {
                reduce { state.copy(locations = intent.locations) }
            }

            is MapIntent.SetMapPadding -> {
                reduce { state.copy(mapPadding = intent.padding) }
            }

            is MapIntent.SetOrderEndsInMinutes -> {
                reduce { state.copy(orderEndsInMinutes = intent.minutes) }
            }

            is MapIntent.SetOrderStatus -> {
                reduce { state.copy(orderStatus = intent.status) }
            }

            is MapIntent.SetRoute -> {
                reduce { state.copy(route = intent.route) }
            }

            is MapIntent.SetViewPadding -> {
                reduce { state.copy(viewPadding = intent.padding) }
            }

            MapIntent.ZoomOut -> {
                postSideEffect(ZoomOut)
            }
        }
    }
}