package uz.yalla.client.core.common.map.static.libre

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.sargunv.maplibrecompose.compose.MaplibreMap
import dev.sargunv.maplibrecompose.compose.rememberCameraState
import dev.sargunv.maplibrecompose.core.CameraPosition
import dev.sargunv.maplibrecompose.core.GestureSettings
import dev.sargunv.maplibrecompose.core.OrnamentSettings
import io.github.dellisd.spatialk.geojson.Position
import kotlinx.coroutines.delay
import org.koin.compose.koinInject
import org.orbitmvi.orbit.compose.collectSideEffect
import uz.yalla.client.core.common.map.core.MapConstants
import uz.yalla.client.core.common.map.extended.libre.LibreMarkers
import uz.yalla.client.core.common.map.extended.libre.fitBounds
import uz.yalla.client.core.common.map.static.StaticMap
import uz.yalla.client.core.common.map.static.intent.StaticMapEffect
import uz.yalla.client.core.common.map.static.intent.StaticMapIntent
import uz.yalla.client.core.common.map.static.model.StaticMapViewModel
import uz.yalla.client.core.common.map.static.model.onIntent
import uz.yalla.client.core.domain.local.AppPreferences
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
        var mapReadyEmitted by remember { mutableStateOf(false) }

        LaunchedEffect(camera) {
            camera.awaitInitialized()
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
                isLogoEnabled = true,
                logoAlignment = Alignment.BottomStart,
                isAttributionEnabled = false,
                isCompassEnabled = false,
                isScaleBarEnabled = false
            ),
            gestureSettings = GestureSettings(
                isScrollGesturesEnabled = false,
                isZoomGesturesEnabled = false,
                isRotateGesturesEnabled = false,
                isTiltGesturesEnabled = false,
                isKeyboardGesturesEnabled = false
            )
        ) {
            LibreMarkers(
                isSystemInDark = effectiveTheme == ThemeType.DARK,
                route = state.route,
                locations = state.locations,
                orderStatus = null
            )
        }
    }
}