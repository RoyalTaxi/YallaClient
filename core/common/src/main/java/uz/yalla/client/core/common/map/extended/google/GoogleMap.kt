package uz.yalla.client.core.common.map.extended.google

import android.Manifest
import android.content.pm.PackageManager
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.*
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
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
import uz.yalla.client.core.common.utils.dpToPx
import uz.yalla.client.core.domain.model.MapPoint
import uz.yalla.client.core.domain.model.OrderStatus

class GoogleMap : Map {

    @OptIn(FlowPreview::class)
    @Composable
    override fun View() {
        val context = LocalContext.current
        val viewModel = koinInject<MapViewModel>()
        val state by viewModel.container.stateFlow.collectAsStateWithLifecycle()
        val camera = rememberCameraPositionState()

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
                            isByUser = camera.cameraMoveStartedReason in listOf(
                                CameraMoveStartedReason.GESTURE,
                                CameraMoveStartedReason.API_ANIMATION
                            )
                        )
                    )
                )
                if (!isMoving) {
                    logicalFocus = camera.position.target
                }
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
                    logicalFocus = builder.build().center
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
                    logicalFocus = builder.build().center
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

        LaunchedEffect(state.viewPadding) {
            logicalFocus?.let { focus ->
                camera.move(CameraUpdateFactory.newLatLngZoom(focus, camera.position.zoom))
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
                mapStyleOptions = null,
                mapType = MapType.NORMAL,
                minZoomPreference = MapConstants.ZOOM_MIN_ZOOM
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
            Markers(
                route = state.route,
                locations = state.locations,
                orderStatus = state.orderStatus,
                carArrivesInMinutes = state.carArrivesInMinutes.takeIf { state.orderStatus == null },
                orderEndsInMinutes = state.orderEndsInMinutes.takeIf { state.orderStatus == null }
            )

            if (driversVisibility) {
                Driver(driver = state.driver)
                DriversWithAnimation(drivers = state.drivers)
            }
        }
    }
}