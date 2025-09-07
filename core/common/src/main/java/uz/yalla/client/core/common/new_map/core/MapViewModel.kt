package uz.yalla.client.core.common.new_map.core

import android.content.Context
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import uz.yalla.client.core.common.new_map.core.MapEffect.AnimateFitBounds
import uz.yalla.client.core.common.new_map.core.MapEffect.AnimateTo
import uz.yalla.client.core.common.new_map.core.MapEffect.AnimateToWithZoom
import uz.yalla.client.core.common.new_map.core.MapEffect.FitBounds
import uz.yalla.client.core.common.new_map.core.MapEffect.MoveTo
import uz.yalla.client.core.common.new_map.core.MapEffect.MoveToWithZoom
import uz.yalla.client.core.common.new_map.core.MapEffect.ZoomOut
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

    fun onIntent(intent: MyMapIntent) = intent {
        when (intent) {
            MyMapIntent.MapReady -> {

            }

            is MyMapIntent.SetMarkerState -> {
                reduce { state.copy(markerState = intent.markerState) }
            }

            MyMapIntent.MoveToFirstLocation -> {
                state.locations.firstOrNull()?.let { point ->
                    postSideEffect(MoveTo(point))
                }
            }

            MyMapIntent.AnimateToFirstLocation -> {
                state.locations.firstOrNull()?.let { point ->
                    postSideEffect(AnimateTo(point))
                }
            }

            MyMapIntent.MoveToMyLocation -> {
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

            MyMapIntent.AnimateToMyLocation -> {
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

            MyMapIntent.MoveToMyRoute -> {
                postSideEffect(FitBounds(state.route))
            }

            MyMapIntent.AnimateToMyRoute -> {
                postSideEffect(
                    AnimateFitBounds(
                        points = state.route,
                        durationMs = MapConstants.ANIMATION_DURATION_MS
                    )
                )
            }

            is MyMapIntent.SetCarArrivesInMinutes -> {
                reduce { state.copy(carArrivesInMinutes = intent.minutes) }
            }

            is MyMapIntent.SetDriver -> {
                reduce { state.copy(driver = intent.driver) }
            }

            is MyMapIntent.SetDrivers -> {
                reduce { state.copy(drivers = intent.drivers) }
            }

            is MyMapIntent.SetLocations -> {
                reduce { state.copy(locations = intent.locations) }
            }

            is MyMapIntent.SetMapPadding -> {
                reduce { state.copy(mapPadding = intent.padding) }
            }

            is MyMapIntent.SetOrderEndsInMinutes -> {
                reduce { state.copy(orderEndsInMinutes = intent.minutes) }
            }

            is MyMapIntent.SetOrderStatus -> {
                reduce { state.copy(orderStatus = intent.status) }
            }

            is MyMapIntent.SetRoute -> {
                reduce { state.copy(route = intent.route) }
            }

            is MyMapIntent.SetViewPadding -> {
                reduce { state.copy(viewPadding = intent.padding) }
            }

            MyMapIntent.ZoomOut -> {
                postSideEffect(ZoomOut)
            }
        }
    }
}