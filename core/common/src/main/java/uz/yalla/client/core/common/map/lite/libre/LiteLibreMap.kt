package uz.yalla.client.core.common.map.lite.libre

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import uz.yalla.client.core.common.map.core.MapConstants
import uz.yalla.client.core.common.map.core.MarkerState
import uz.yalla.client.core.common.map.core.plus
import uz.yalla.client.core.common.map.extended.libre.animateTo
import uz.yalla.client.core.common.map.extended.libre.moveTo
import uz.yalla.client.core.common.map.lite.LiteMap
import uz.yalla.client.core.common.map.lite.intent.LiteMapEffect
import uz.yalla.client.core.common.map.lite.intent.LiteMapIntent
import uz.yalla.client.core.common.map.lite.model.LiteMapViewModel
import uz.yalla.client.core.domain.local.AppPreferences
import uz.yalla.client.core.domain.model.MapPoint
import uz.yalla.client.core.domain.model.type.ThemeType
import kotlin.time.Duration.Companion.milliseconds

class LiteLibreMap : LiteMap {
    @Composable
    override fun View(
        modifier: Modifier,
        viewModel: LiteMapViewModel,
    ) {
        val appPreferences = koinInject<AppPreferences>()
        val state by viewModel.container.stateFlow.collectAsStateWithLifecycle()
        val initialLocation by appPreferences.entryLocation.collectAsStateWithLifecycle(0.0 to 0.0)
        val isSystemDark = isSystemInDarkTheme()
        val prefTheme by appPreferences.themeType.collectAsStateWithLifecycle(initialValue = ThemeType.LIGHT)
        val effectiveTheme = when (prefTheme) {
            ThemeType.SYSTEM -> if (isSystemDark) ThemeType.DARK else ThemeType.LIGHT
            else -> prefTheme
        }
        val camera = rememberCameraState(
            firstPosition = CameraPosition(
                target = Position(
                    longitude = initialLocation.second,
                    latitude = initialLocation.first
                )
            )
        )
        val animationScope = rememberCoroutineScope()
        var animationJob by remember { mutableStateOf<Job?>(null) }
        var logicalFocus by remember { mutableStateOf<Position?>(null) }
        var mapReadyEmitted by remember { mutableStateOf(false) }

        LaunchedEffect(Unit) {
            snapshotFlow { camera.isCameraMoving }.collectLatest { isMoving ->
                viewModel.onIntent(
                    LiteMapIntent.SetMarkerState(
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

        LaunchedEffect(camera) {
            camera.awaitInitialized()
            delay(100)
            if (!mapReadyEmitted) {
                mapReadyEmitted = true
                viewModel.onIntent(LiteMapIntent.MapReady)
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

        viewModel.collectSideEffect { effect ->
            fun cancelAndLaunch(block: suspend () -> Unit) {
                animationJob?.cancel()
                animationJob = animationScope.launch { block() }
            }

            when (effect) {
                is LiteMapEffect.MoveTo -> {
                    cancelAndLaunch {
                        camera.moveTo(
                            point = effect.point,
                            padding = state.viewPadding + PaddingValues(state.mapPadding.dp),
                            zoom = MapConstants.DEFAULT_ZOOM.toDouble()
                        )
                        logicalFocus = Position(
                            longitude = effect.point.lng,
                            latitude = effect.point.lat
                        )
                    }
                }

                is LiteMapEffect.AnimateTo -> {
                    cancelAndLaunch {
                        camera.animateTo(
                            point = effect.point,
                            padding = state.viewPadding + PaddingValues(state.mapPadding.dp),
                            zoom = MapConstants.DEFAULT_ZOOM.toDouble()
                        )
                    }
                    logicalFocus = Position(
                        longitude = effect.point.lng,
                        latitude = effect.point.lat
                    )
                }
            }
        }

        MaplibreMap(
            modifier = modifier,
            cameraState = camera,
            zoomRange = MapConstants.ZOOM_MIN_ZOOM..MapConstants.ZOOM_MAX_ZOOM,
            styleUri = if (effectiveTheme == ThemeType.DARK) {
                "https://basemaps.cartocdn.com/gl/dark-matter-gl-style/style.json"
            } else {
                "https://basemaps.cartocdn.com/gl/positron-gl-style/style.json"
            },
            ornamentSettings = OrnamentSettings(
                padding = state.viewPadding,
                isLogoEnabled = true,
                logoAlignment = Alignment.BottomStart,
                isAttributionEnabled = false,
                isCompassEnabled = false,
                isScaleBarEnabled = false
            ),
            gestureSettings = GestureSettings(
                isScrollGesturesEnabled = true,
                isZoomGesturesEnabled = true,
                isRotateGesturesEnabled = false,
                isTiltGesturesEnabled = false,
                isKeyboardGesturesEnabled = false
            )
        )
    }
}