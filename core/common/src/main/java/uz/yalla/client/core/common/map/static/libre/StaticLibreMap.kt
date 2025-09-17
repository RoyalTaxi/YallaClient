package uz.yalla.client.core.common.map.static.libre

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.dellisd.spatialk.geojson.Position
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import org.maplibre.compose.camera.CameraPosition
import org.maplibre.compose.camera.rememberCameraState
import org.maplibre.compose.map.GestureOptions
import org.maplibre.compose.map.MapOptions
import org.maplibre.compose.map.MaplibreMap
import org.maplibre.compose.map.OrnamentOptions
import org.maplibre.compose.style.BaseStyle
import org.orbitmvi.orbit.compose.collectSideEffect
import uz.yalla.client.core.common.map.core.MapConstants
import uz.yalla.client.core.common.map.extended.libre.LibreMarkers
import uz.yalla.client.core.common.map.extended.libre.fitBounds
import uz.yalla.client.core.common.map.extended.libre.moveTo
import uz.yalla.client.core.common.map.static.StaticMap
import uz.yalla.client.core.common.map.static.intent.StaticMapEffect
import uz.yalla.client.core.common.map.static.intent.StaticMapIntent
import uz.yalla.client.core.common.map.static.model.StaticMapViewModel
import uz.yalla.client.core.common.map.static.model.onIntent
import uz.yalla.client.core.domain.local.AppPreferences
import uz.yalla.client.core.domain.model.MapPoint
import uz.yalla.client.core.domain.model.OrderStatus
import uz.yalla.client.core.domain.model.type.ThemeType

class StaticLibreMap : StaticMap {
    @Composable
    override fun View(
        modifier: Modifier,
        viewModel: StaticMapViewModel
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
        val scope = rememberCoroutineScope()
        var mapReadyEmitted by remember { mutableStateOf(false) }

        LaunchedEffect(camera) {
            camera.awaitProjection()
            delay(100)
            if (!mapReadyEmitted) {
                mapReadyEmitted = true
                viewModel.onIntent(StaticMapIntent.MapReady)
            }
        }

        viewModel.collectSideEffect { effect ->
            when (effect) {
                StaticMapEffect.MoveToFitBounds -> {
                    if (state.route.isNotEmpty()) camera.fitBounds(
                        points = state.route,
                        padding = PaddingValues(state.mapPadding.dp)
                    )
                    else if (state.locations.isNotEmpty()) camera.fitBounds(
                        points = state.locations,
                        padding = PaddingValues(state.mapPadding.dp)
                    )
                }

                StaticMapEffect.MoveToFirstLocation -> {
                    state.locations.firstOrNull()?.let { location ->
                        scope.launch {
                            camera.moveTo(
                                point = MapPoint(
                                    lat = location.lat,
                                    lng = location.lng
                                ),
                                padding = PaddingValues(state.mapPadding.dp),
                                zoom = MapConstants.DEFAULT_ZOOM.toDouble()
                            )
                        }
                    }
                }
            }
        }

        MaplibreMap(
            modifier = modifier,
            cameraState = camera,
            zoomRange = MapConstants.ZOOM_MIN_ZOOM..MapConstants.ZOOM_MAX_ZOOM,
            baseStyle = BaseStyle.Uri(
                if (effectiveTheme == ThemeType.DARK) {
                    "https://basemaps.cartocdn.com/gl/dark-matter-gl-style/style.json"
                } else {
                    "https://basemaps.cartocdn.com/gl/positron-gl-style/style.json"
                }
            ),
            options = MapOptions(
                ornamentOptions = OrnamentOptions(
                    isLogoEnabled = true,
                    logoAlignment = Alignment.BottomStart,
                    isAttributionEnabled = false,
                    isCompassEnabled = false,
                    isScaleBarEnabled = false
                ),
                gestureOptions = GestureOptions(
                    isScrollEnabled = false,
                    isZoomEnabled = true,
                    isQuickZoomEnabled = true,
                    isRotateEnabled = false,
                    isTiltEnabled = false
                )
            )
        ) {
            LibreMarkers(
                isSystemInDark = effectiveTheme == ThemeType.DARK,
                route = state.route,
                locations = state.locations,
                orderStatus = OrderStatus.Completed
            )
        }
    }
}