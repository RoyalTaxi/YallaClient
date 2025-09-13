package uz.yalla.client.core.common.map.extended.libre

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.sargunv.maplibrecompose.compose.MaplibreMap
import dev.sargunv.maplibrecompose.compose.rememberCameraState
import dev.sargunv.maplibrecompose.core.CameraMoveReason
import dev.sargunv.maplibrecompose.core.CameraPosition
import dev.sargunv.maplibrecompose.core.GestureSettings
import dev.sargunv.maplibrecompose.core.OrnamentSettings
import io.github.dellisd.spatialk.geojson.Position
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import org.orbitmvi.orbit.compose.collectSideEffect
import uz.yalla.client.core.common.map.core.Map
import uz.yalla.client.core.common.map.core.MapConstants
import uz.yalla.client.core.common.map.core.intent.MapEffect
import uz.yalla.client.core.common.map.core.intent.MapIntent
import uz.yalla.client.core.common.map.core.intent.MarkerState
import uz.yalla.client.core.common.map.core.model.MapViewModel
import uz.yalla.client.core.common.map.core.plus
import uz.yalla.client.core.domain.local.AppPreferences
import uz.yalla.client.core.domain.model.MapPoint
import uz.yalla.client.core.domain.model.OrderStatus
import kotlin.time.Duration.Companion.milliseconds

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
                    longitude = initialLocation.second,
                    latitude = initialLocation.first
                )
            )
        )

        val driversVisibility by remember { derivedStateOf { camera.position.zoom >= 8.0 } }

        val animationScope = rememberCoroutineScope()
        var animationJob by remember { mutableStateOf<Job?>(null) }

        var logicalFocus by remember { mutableStateOf<Position?>(null) }

        LaunchedEffect(Unit) {
            snapshotFlow { camera.isCameraMoving }.collectLatest { isMoving ->
                viewModel.onIntent(
                    MapIntent.SetMarkerState(
                        markerState = MarkerState(
                            point = camera.position.target.let {
                                MapPoint(
                                    it.latitude,
                                    it.longitude
                                )
                            },
                            isMoving = isMoving,
                            isByUser = camera.moveReason == CameraMoveReason.GESTURE
                        )
                    )
                )
                if (!isMoving) {
                    logicalFocus = camera.position.target
                }
            }
        }

        var mapReadyEmitted by remember { mutableStateOf(false) }
        LaunchedEffect(camera) {
            camera.awaitInitialized()
            delay(100)
            if (!mapReadyEmitted) {
                mapReadyEmitted = true
                viewModel.onIntent(MapIntent.MapReady)
            }
        }

        viewModel.collectSideEffect { effect ->
            fun cancelAndLaunch(block: suspend () -> Unit) {
                animationJob?.cancel()
                animationJob = animationScope.launch { block() }
            }

            when (effect) {
                is MapEffect.MoveTo -> {
                    cancelAndLaunch {
                        camera.moveTo(
                            point = effect.point,
                            padding = state.viewPadding + PaddingValues(state.mapPadding.dp)
                        )
                        logicalFocus = Position(
                            longitude = effect.point.lng,
                            latitude = effect.point.lat
                        )
                    }
                }

                is MapEffect.MoveToWithZoom -> {
                    cancelAndLaunch {
                        camera.animateTo(
                            point = effect.point,
                            padding = state.viewPadding + PaddingValues(state.mapPadding.dp),
                            zoom = effect.zoom.toDouble(),
                            durationMs = 1
                        )
                    }
                    logicalFocus = Position(
                        longitude = effect.point.lng,
                        latitude = effect.point.lat
                    )
                }

                is MapEffect.AnimateTo -> {
                    cancelAndLaunch {
                        camera.animateTo(
                            point = effect.point,
                            padding = state.viewPadding + PaddingValues(state.mapPadding.dp)
                        )
                    }
                    logicalFocus = Position(
                        longitude = effect.point.lng,
                        latitude = effect.point.lat
                    )
                }

                is MapEffect.AnimateToWithZoom -> {
                    cancelAndLaunch {
                        camera.animateTo(
                            point = effect.point,
                            padding = state.viewPadding + PaddingValues(state.mapPadding.dp),
                            zoom = effect.zoom.toDouble()
                        )
                    }
                    logicalFocus = Position(
                        longitude = effect.point.lng,
                        latitude = effect.point.lat
                    )
                }

                is MapEffect.AnimateToWithZoomAndDuration -> {
                    cancelAndLaunch {
                        camera.animateTo(
                            point = effect.point,
                            padding = state.viewPadding + PaddingValues(state.mapPadding.dp),
                            zoom = effect.zoom.toDouble(),
                            durationMs = effect.durationMs
                        )
                    }
                    logicalFocus = Position(
                        longitude = effect.point.lng,
                        latitude = effect.point.lat
                    )
                }

                is MapEffect.FitBounds -> cancelAndLaunch {
                    camera.fitBounds(
                        points = effect.points,
                        padding = state.viewPadding + PaddingValues(state.mapPadding.dp)
                    )
                }

                is MapEffect.AnimateFitBounds -> cancelAndLaunch {
                    camera.animateFitBounds(
                        points = effect.points,
                        padding = state.viewPadding + PaddingValues(state.mapPadding.dp),
                        durationMs = effect.durationMs
                    )
                }

                MapEffect.ZoomOut -> {
                    if (camera.position.zoom > MapConstants.SEARCH_MIN_ZOOM.toDouble()) {
                        camera.zoomOut()
                    } else {
                        animationJob?.cancel()
                    }
                }
            }
        }

        LaunchedEffect(state.viewPadding) {
            logicalFocus?.let { focus ->
                camera.animateTo(
                    finalPosition = CameraPosition(
                        target = focus,
                        zoom = camera.position.zoom,
                        padding = state.viewPadding
                    ),
                    duration = 1.milliseconds
                )
            }
        }

        MaplibreMap(
            cameraState = camera,
            zoomRange = MapConstants.ZOOM_MIN_ZOOM..MapConstants.ZOOM_MAX_ZOOM,
            styleUri = "https://basemaps.cartocdn.com/gl/voyager-gl-style/style.json",
            ornamentSettings = OrnamentSettings(
                padding = state.viewPadding,
                isLogoEnabled = true,
                logoAlignment = Alignment.BottomStart,
                isAttributionEnabled = false,
                isCompassEnabled = false,
                isScaleBarEnabled = false
            ),
            gestureSettings = GestureSettings(
                isScrollGesturesEnabled = state.orderStatus !in OrderStatus.nonInteractive,
                isZoomGesturesEnabled = state.orderStatus !in OrderStatus.nonInteractive,
                isRotateGesturesEnabled = false,
                isTiltGesturesEnabled = false,
                isKeyboardGesturesEnabled = false
            ),
            modifier = Modifier.onSizeChanged {
                animationScope.launch {
                    camera.animateTo(
                        finalPosition = CameraPosition(
                            target = camera.position.target,
                            zoom = camera.position.zoom,
                            padding = state.viewPadding
                        ),
                        duration = 1.milliseconds
                    )
                }
            }
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
