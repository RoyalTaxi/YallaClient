package uz.yalla.client.core.common.map.lite.model

import android.content.Context
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import uz.yalla.client.core.common.map.core.MarkerState
import uz.yalla.client.core.common.map.lite.intent.LiteMapEffect
import uz.yalla.client.core.common.map.lite.intent.LiteMapIntent
import uz.yalla.client.core.common.map.lite.intent.LiteMapState
import uz.yalla.client.core.common.utils.getCurrentLocation
import uz.yalla.client.core.common.viewmodel.BaseViewModel
import uz.yalla.client.core.domain.model.MapPoint

class LiteMapViewModel(
    private val appContext: Context,
    private val initialLocation: MapPoint?
) : BaseViewModel(), ContainerHost<LiteMapState, LiteMapEffect> {
    override val container: Container<LiteMapState, LiteMapEffect> = container(LiteMapState.INITIAL)

    val markerState: Flow<MarkerState> =
        container.stateFlow
            .map { it.markerState }
            .distinctUntilChanged()

    val isMapReady: Flow<Boolean> =
        container.stateFlow
            .map { it.isMapReady }
            .distinctUntilChanged()

    fun onIntent(intent: LiteMapIntent) = intent {
        when (intent) {
            LiteMapIntent.MapReady -> {
                initialLocation?.let { location ->
                    postSideEffect(LiteMapEffect.MoveTo(location))
                } ?: run {
                    getCurrentLocation(context = appContext) {
                        viewModelScope.launch {
                            postSideEffect(
                                LiteMapEffect.MoveTo(MapPoint(it.latitude, it.longitude))
                            )
                        }
                    }
                }
                reduce { state.copy(isMapReady = true) }
            }

            LiteMapIntent.AnimateToMyLocation -> {
                getCurrentLocation(
                    context = appContext,
                    onLocationFetched = { latLng ->
                        viewModelScope.launch {
                            postSideEffect(
                                LiteMapEffect.AnimateTo(
                                    point = MapPoint(latLng.latitude, latLng.longitude)
                                )
                            )
                        }
                    }
                )
            }

            LiteMapIntent.MoveToMyLocation -> {
                getCurrentLocation(
                    context = appContext,
                    onLocationFetched = { latLng ->
                        viewModelScope.launch {
                            postSideEffect(
                                LiteMapEffect.MoveTo(
                                    point = MapPoint(latLng.latitude, latLng.longitude)
                                )
                            )
                        }
                    }
                )
            }

            is LiteMapIntent.SetLocation -> reduce { state.copy(location = intent.location) }
            is LiteMapIntent.SetMapPadding -> reduce { state.copy(mapPadding = intent.padding) }
            is LiteMapIntent.SetMarkerState -> reduce { state.copy(markerState = intent.markerState) }
        }
    }
}