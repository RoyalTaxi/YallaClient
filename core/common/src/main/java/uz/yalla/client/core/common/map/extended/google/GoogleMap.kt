package uz.yalla.client.core.common.map.extended.google

import android.Manifest
import android.content.pm.PackageManager
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.CameraMoveStartedReason
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import org.orbitmvi.orbit.compose.collectSideEffect
import uz.yalla.client.core.common.R
import uz.yalla.client.core.common.map.core.MapConstants
import uz.yalla.client.core.common.map.core.MarkerState
import uz.yalla.client.core.common.map.extended.Map
import uz.yalla.client.core.common.map.extended.intent.MapEffect
import uz.yalla.client.core.common.map.extended.intent.MapIntent
import uz.yalla.client.core.common.map.extended.model.MapViewModel
import uz.yalla.client.core.common.utils.dpToPx
import uz.yalla.client.core.domain.local.AppPreferences
import uz.yalla.client.core.domain.model.MapPoint
import uz.yalla.client.core.domain.model.OrderStatus
import uz.yalla.client.core.domain.model.type.ThemeType

class GoogleMap : Map {

    @OptIn(FlowPreview::class)
    @Composable
    override fun View() {
        val context = LocalContext.current
        val viewModel = koinInject<MapViewModel>()
        val appPreferences = koinInject<AppPreferences>()
        val state by viewModel.container.stateFlow.collectAsStateWithLifecycle()
        val camera = rememberCameraPositionState()
        val isSystemDark = isSystemInDarkTheme()
        val prefTheme by appPreferences.themeType.collectAsStateWithLifecycle(initialValue = ThemeType.LIGHT)
        val effectiveTheme = when (prefTheme) {
            ThemeType.SYSTEM -> if (isSystemDark) ThemeType.DARK else ThemeType.LIGHT
            else -> prefTheme
        }
        val hasLocationPermission =
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
        val driversVisibility by remember {
            derivedStateOf { camera.position.zoom >= 8f }
        }
        val animationScope = rememberCoroutineScope()
        var animationJob by remember { mutableStateOf<Job?>(null) }
        var logicalFocus by remember { mutableStateOf<LatLng?>(null) }

        LaunchedEffect(Unit) {
            snapshotFlow { camera.isMoving }.collectLatest { isMoving ->
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
                            isByUser = camera.cameraMoveStartedReason == CameraMoveStartedReason.GESTURE
                        )
                    )
                )
                if (!isMoving) {
                    logicalFocus = camera.position.target
                }
            }
        }

        LaunchedEffect(state.viewPadding) {
            logicalFocus?.let { focus ->
                camera.move(CameraUpdateFactory.newLatLngZoom(focus, camera.position.zoom))
            }
        }

        viewModel.collectSideEffect { effect ->
            fun cancelAndLaunch(block: suspend () -> Unit) {
                animationJob?.cancel()
                animationJob = animationScope.launch { block() }
            }

            when (effect) {
                is MapEffect.MoveTo -> {
                    animationJob?.cancel()
                    camera.moveTo(point = effect.point)
                    logicalFocus = LatLng(effect.point.lat, effect.point.lng)
                }

                is MapEffect.MoveToWithZoom -> {
                    animationJob?.cancel()
                    camera.moveTo(point = effect.point, zoom = effect.zoom.toFloat())
                    logicalFocus = LatLng(effect.point.lat, effect.point.lng)
                }

                is MapEffect.AnimateTo -> {
                    cancelAndLaunch { camera.animateTo(point = effect.point) }
                    logicalFocus = LatLng(effect.point.lat, effect.point.lng)
                }

                is MapEffect.AnimateToWithZoom -> {
                    cancelAndLaunch {
                        camera.animateTo(
                            point = effect.point,
                            zoom = effect.zoom.toFloat()
                        )
                    }
                    logicalFocus = LatLng(effect.point.lat, effect.point.lng)
                }

                is MapEffect.AnimateToWithZoomAndDuration -> {
                    cancelAndLaunch {
                        camera.animateTo(
                            point = effect.point,
                            zoom = effect.zoom.toFloat(),
                            durationMs = effect.durationMs
                        )
                    }
                    logicalFocus = LatLng(effect.point.lat, effect.point.lng)
                }

                is MapEffect.FitBounds -> {
                    animationJob?.cancel()
                    camera.fitBounds(
                        points = effect.points,
                        padding = dpToPx(context = context, dp = state.mapPadding)
                    )
                    val builder = LatLngBounds.Builder()
                    effect.points.forEach { builder.include(LatLng(it.lat, it.lng)) }
                }

                is MapEffect.AnimateFitBounds -> {
                    cancelAndLaunch {
                        camera.animateFitBounds(
                            points = effect.points,
                            padding = dpToPx(context, state.mapPadding),
                            durationMs = effect.durationMs
                        )
                    }
                    val builder = LatLngBounds.Builder()
                    effect.points.forEach { builder.include(LatLng(it.lat, it.lng)) }
                }

                MapEffect.ZoomOut -> {
                    if (camera.position.zoom > MapConstants.SEARCH_MIN_ZOOM) {
                        cancelAndLaunch { camera.zoomOut() }
                    } else {
                        animationJob?.cancel()
                    }
                }
            }
        }

        GoogleMap(
            cameraPositionState = camera,
            contentPadding = state.viewPadding,
            properties = MapProperties(
                isBuildingEnabled = true,
                isIndoorEnabled = true,
                isMyLocationEnabled = hasLocationPermission,
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
                scrollGesturesEnabled = state.orderStatus !in OrderStatus.nonInteractive,
                scrollGesturesEnabledDuringRotateOrZoom = false,
                tiltGesturesEnabled = false,
                zoomControlsEnabled = false,
                zoomGesturesEnabled = state.orderStatus !in OrderStatus.nonInteractive
            ),
            onMapLoaded = { viewModel.onIntent(MapIntent.MapReady) },
            modifier = Modifier.onSizeChanged {
                camera.move(CameraUpdateFactory.newCameraPosition(camera.position))
            }
        ) {
            GoogleMarkers(
                isSystemInDark = effectiveTheme == ThemeType.DARK,
                route = state.route,
                locations = state.locations,
                orderStatus = state.orderStatus,
                carArrivesInMinutes = state.carArrivesInMinutes.takeIf { state.orderStatus == null },
                orderEndsInMinutes = state.orderEndsInMinutes.takeIf { state.orderStatus == null }
            )

            GoogleDriver(driver = state.driver)
            if (driversVisibility) {
                GoogleDriversWithAnimation(drivers = state.drivers)
            }
        }
    }
}
