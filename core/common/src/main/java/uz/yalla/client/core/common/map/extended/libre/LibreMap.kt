package uz.yalla.client.core.common.map.extended.libre

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.sargunv.maplibrecompose.compose.MaplibreMap
import dev.sargunv.maplibrecompose.compose.rememberCameraState
import dev.sargunv.maplibrecompose.core.CameraPosition
import io.github.dellisd.spatialk.geojson.Position
import org.koin.compose.koinInject
import org.orbitmvi.orbit.compose.collectSideEffect
import uz.yalla.client.core.common.map.core.Map
import uz.yalla.client.core.common.map.core.MapConstants
import uz.yalla.client.core.common.map.core.intent.MapEffect
import uz.yalla.client.core.common.map.core.model.MapViewModel
import uz.yalla.client.core.domain.local.AppPreferences


class LibreMap : Map {

    @Composable
    override fun View() {
        val viewModel = koinInject<MapViewModel>()
        val appPreferences = koinInject<AppPreferences>()
        val state by viewModel.container.stateFlow.collectAsStateWithLifecycle()
        val initialLocation by appPreferences.entryLocation.collectAsStateWithLifecycle(0.0 to 0.0)
        val camera = rememberCameraState(
            firstPosition = CameraPosition(
                target = Position(
                    longitude = initialLocation.first,
                    latitude = initialLocation.second
                )
            )
        )

        val driversVisibility by remember(camera.position.zoom) {
            mutableStateOf(camera.position.zoom >= 8)
        }

        viewModel.collectSideEffect { effect ->
            when (effect) {
                is MapEffect.MoveTo -> {
                    camera.moveTo(
                        point = effect.point,
                        padding = state.viewPadding
                    )
                }

                is MapEffect.MoveToWithZoom -> {
                    camera.animateTo(
                        point = effect.point,
                        padding = state.viewPadding,
                        zoom = effect.zoom.toDouble(),
                        durationMs = 1
                    )
                }

                is MapEffect.AnimateTo -> {
                    camera.animateTo(
                        point = effect.point,
                        padding = state.viewPadding
                    )
                }

                is MapEffect.AnimateToWithZoom -> {
                    camera.animateTo(
                        point = effect.point,
                        padding = state.viewPadding,
                        zoom = effect.zoom.toDouble()
                    )
                }

                is MapEffect.AnimateToWithZoomAndDuration -> {
                    camera.animateTo(
                        point = effect.point,
                        padding = state.viewPadding,
                        zoom = effect.zoom.toDouble(),
                        durationMs = effect.durationMs
                    )
                }

                is MapEffect.FitBounds -> {
                    camera.fitBounds(
                        points = effect.points,
                        padding = state.viewPadding
                    )
                }

                is MapEffect.AnimateFitBounds -> {
                    camera.animateFitBounds(
                        points = effect.points,
                        padding = state.viewPadding,
                        durationMs = effect.durationMs
                    )
                }

                MapEffect.ZoomOut -> {
                    if (camera.position.zoom > MapConstants.SEARCH_MIN_ZOOM) {
                        camera.zoomOut()
                    }
                }
            }
        }

        MaplibreMap(
            modifier = Modifier.fillMaxSize(),
            cameraState = camera,
            styleUri = "https://basemaps.cartocdn.com/gl/voyager-gl-style/style.json",
        ) {
            Markers(
                route = state.route,
                locations = state.locations,
                orderStatus = state.orderStatus,
                carArrivesInMinutes = state.carArrivesInMinutes.takeIf { state.orderStatus == null },
                orderEndsInMinutes = state.orderEndsInMinutes.takeIf { state.orderStatus == null }
            )

            if (driversVisibility) {
                DriverLibre(state.driver)
                DriversWithAnimationLibre(state.drivers)
            }
        }
    }
}