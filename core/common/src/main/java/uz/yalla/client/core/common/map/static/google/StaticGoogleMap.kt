package uz.yalla.client.core.common.map.static.google

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.*
import kotlinx.coroutines.FlowPreview
import org.koin.compose.koinInject
import org.orbitmvi.orbit.compose.collectSideEffect
import uz.yalla.client.core.common.R
import uz.yalla.client.core.common.map.core.MapConstants
import uz.yalla.client.core.common.map.extended.google.fitBounds
import uz.yalla.client.core.common.map.static.StaticMap
import uz.yalla.client.core.common.map.static.intent.StaticMapEffect
import uz.yalla.client.core.common.map.static.intent.StaticMapIntent
import uz.yalla.client.core.common.map.static.model.StaticMapViewModel
import uz.yalla.client.core.domain.local.AppPreferences
import uz.yalla.client.core.domain.model.type.ThemeType

class StaticGoogleMap : StaticMap {

    @OptIn(FlowPreview::class)
    @Composable
    override fun View(
        modifier: Modifier,
        viewModel: StaticMapViewModel
    ) {
        val context = LocalContext.current
        val appPreferences = koinInject<AppPreferences>()
        val camera = rememberCameraPositionState()
        val state by viewModel.container.stateFlow.collectAsStateWithLifecycle()
        val isSystemDark = isSystemInDarkTheme()
        val prefTheme by appPreferences.themeType.collectAsStateWithLifecycle(initialValue = ThemeType.LIGHT)
        val effectiveTheme = when (prefTheme) {
            ThemeType.SYSTEM -> if (isSystemDark) ThemeType.DARK else ThemeType.LIGHT
            else -> prefTheme
        }

        viewModel.collectSideEffect { effect ->
            when (effect) {
                StaticMapEffect.MoveToFitBounds -> camera.fitBounds(state.route, state.mapPadding)
            }
        }

        GoogleMap(
            modifier = modifier,
            cameraPositionState = camera,
            properties = MapProperties(
                isBuildingEnabled = true,
                isIndoorEnabled = true,
                isMyLocationEnabled = false,
                isTrafficEnabled = false,
                latLngBoundsForCameraTarget = LatLngBounds(
                    LatLng(37.184, 55.997),
                    LatLng(45.590, 73.139)
                ),
                mapStyleOptions = if (effectiveTheme == ThemeType.DARK) {
                    MapStyleOptions.loadRawResourceStyle(context, R.raw.google_dark_map)
                } else null,
                mapType = MapType.NORMAL,
                minZoomPreference = MapConstants.ZOOM_MIN_ZOOM,
                maxZoomPreference = MapConstants.ZOOM_MAX_ZOOM
            ),
            uiSettings = MapUiSettings(
                compassEnabled = false,
                indoorLevelPickerEnabled = true,
                mapToolbarEnabled = false,
                myLocationButtonEnabled = false,
                rotationGesturesEnabled = false,
                scrollGesturesEnabled = false,
                scrollGesturesEnabledDuringRotateOrZoom = false,
                tiltGesturesEnabled = false,
                zoomControlsEnabled = false,
                zoomGesturesEnabled = false
            ),
            onMapLoaded = { viewModel.onIntent(StaticMapIntent.MapReady) },
        )
    }
}