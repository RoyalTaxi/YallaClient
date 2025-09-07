package uz.yalla.client.core.common.new_map.google

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.Dash
import com.google.android.gms.maps.model.Gap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.CameraMoveStartedReason
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerComposable
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.orbitmvi.orbit.compose.collectSideEffect
import uz.yalla.client.core.common.R
import uz.yalla.client.core.common.marker.rememberGoogleMarkerWithInfo
import uz.yalla.client.core.common.new_map.core.Map
import uz.yalla.client.core.common.new_map.core.MapConstants
import uz.yalla.client.core.common.new_map.core.MapEffect
import uz.yalla.client.core.common.new_map.core.MapViewModel
import uz.yalla.client.core.common.new_map.core.MarkerState
import uz.yalla.client.core.common.new_map.core.MyMapIntent
import uz.yalla.client.core.common.utils.dpToPx
import uz.yalla.client.core.common.utils.findClosestPointOnRoute
import uz.yalla.client.core.common.utils.getCurrentLocation
import uz.yalla.client.core.common.utils.vectorToBitmapDescriptor
import uz.yalla.client.core.domain.model.Executor
import uz.yalla.client.core.domain.model.MapPoint
import uz.yalla.client.core.domain.model.OrderStatus
import uz.yalla.client.core.presentation.design.theme.YallaTheme
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

@Composable
private fun Markers(
    route: List<MapPoint>,
    orderStatus: OrderStatus?,
    locations: List<MapPoint>,
    carArrivesInMinutes: Int? = null,
    orderEndsInMinutes: Int? = null
) {
    val context = LocalContext.current

    val originIcon = key(
        locations.firstOrNull()?.hashCode()?.plus(carArrivesInMinutes.hashCode()),
    ) {
        rememberGoogleMarkerWithInfo(
            key = "origin_${locations.firstOrNull()?.hashCode()}",
            title = carArrivesInMinutes?.let { stringResource(R.string.x_min, it.toString()) },
            description = stringResource(R.string.coming),
            infoColor = YallaTheme.color.primary,
            pointColor = YallaTheme.color.gray,
            pointBackgroundColor = YallaTheme.color.background
        )
    }

    val middleIcon = remember {
        vectorToBitmapDescriptor(context, R.drawable.ic_middle_marker)
            ?: BitmapDescriptorFactory.defaultMarker()
    }

    val endIcon = key(
        locations.lastOrNull()?.hashCode()?.plus(orderEndsInMinutes.hashCode())
    ) {
        rememberGoogleMarkerWithInfo(
            key = "destination_${locations.lastOrNull()?.hashCode()}",
            title = orderEndsInMinutes?.let { stringResource(R.string.x_min, it.toString()) },
            description = stringResource(R.string.on_the_way),
            infoColor = YallaTheme.color.black,
            pointColor = YallaTheme.color.primary,
            pointBackgroundColor = YallaTheme.color.background
        )
    }

    if (route.isNotEmpty()) {
        val isDarkTheme = isSystemInDarkTheme()
        Polyline(
            points = route.map { LatLng(it.lat, it.lng) },
            color = if (isDarkTheme) Color.White else Color.Black
        )
    }

    if (route.isNotEmpty() && orderStatus !in OrderStatus.cancellable) {
        locations.forEachIndexed { index, location ->
            val target: MapPoint? = when (index) {
                0 -> route.firstOrNull()
                locations.lastIndex -> route.lastOrNull()
                else -> findClosestPointOnRoute(location, route)
            }

            if (target != null && target != location) {
                Polyline(
                    points = listOf(
                        LatLng(location.lat, location.lng),
                        LatLng(target.lat, target.lng)
                    ),
                    color = YallaTheme.color.gray,
                    pattern = listOf(Dash(16f), Gap(16f))
                )
            }
        }
    }

    if (locations.isEmpty()) return

    if ((orderStatus != null && orderStatus in OrderStatus.nonInteractive) || route.isNotEmpty()) {
        val start = locations.first()
        key(start.hashCode()) {
            Marker(
                state = rememberMarkerState(position = LatLng(start.lat, start.lng)),
                icon = originIcon,
                zIndex = 2f
            )
        }
    }

    if (locations.size > 2) {
        locations.subList(1, locations.lastIndex).forEachIndexed { index, mid ->
            key(mid.hashCode(), index) {
                Marker(
                    state = rememberMarkerState(position = LatLng(mid.lat, mid.lng)),
                    icon = middleIcon,
                    zIndex = 2f
                )
            }
        }
    }

    if (locations.size > 1) {
        val end = locations.last()
        key(end.hashCode()) {
            Marker(
                state = rememberMarkerState(position = LatLng(end.lat, end.lng)),
                icon = endIcon,
                zIndex = 2f
            )
        }
    }
}

@Composable
private fun Driver(
    driver: Executor?
) {
    driver?.let {
        MarkerComposable(
            flat = true,
            rotation = it.heading.toFloat(),
            state = rememberMarkerState(position = LatLng(it.lat, it.lng)),
            anchor = Offset(0.5f, 0.5f),
            zIndex = 1f
        ) {
            Icon(
                painter = painterResource(R.drawable.img_car_marker),
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun DriversWithAnimation(
    drivers: List<Executor>
) {
    drivers.take(20).forEach { driver ->
        key(driver.id) {
            val markerState = rememberMarkerState(key = driver.id.toString())

            val animatedLat = remember { Animatable(driver.lat.toFloat()) }
            val animatedLng = remember { Animatable(driver.lng.toFloat()) }
            val animatedHeading = remember { Animatable(driver.heading.toFloat()) }

            val isAnimating = remember { mutableStateOf(false) }

            LaunchedEffect(driver.lat, driver.lng, driver.heading) {
                if (!isAnimating.value) {
                    isAnimating.value = true

                    val distance = calculateDistance(
                        animatedLat.value.toDouble(),
                        animatedLng.value.toDouble(),
                        driver.lat,
                        driver.lng
                    )

                    val duration = when {
                        distance < 0.0005 -> 300
                        distance < 0.002 -> 800
                        distance < 0.01 -> 1200
                        else -> 1800
                    }

                    launch {
                        animatedLat.animateTo(
                            targetValue = driver.lat.toFloat(),
                            animationSpec = tween(duration, easing = FastOutSlowInEasing)
                        )
                    }

                    launch {
                        animatedLng.animateTo(
                            targetValue = driver.lng.toFloat(),
                            animationSpec = tween(duration, easing = FastOutSlowInEasing)
                        )
                    }

                    launch {
                        val targetHeading = normalizeHeading(driver.heading.toFloat())
                        animatedHeading.animateTo(
                            targetValue = targetHeading,
                            animationSpec = tween(
                                (duration * 1.1).toInt(),
                                easing = FastOutSlowInEasing
                            )
                        )
                    }

                    isAnimating.value = false
                }
            }

            markerState.position =
                LatLng(animatedLat.value.toDouble(), animatedLng.value.toDouble())

            MarkerComposable(
                state = markerState,
                rotation = animatedHeading.value,
                flat = true,
                anchor = Offset(0.5f, 0.5f),
                zIndex = 1f
            ) {
                Icon(
                    painter = painterResource(R.drawable.img_car_marker),
                    contentDescription = "Driver",
                    tint = Color.Unspecified,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

private fun calculateDistance(
    lat1: Double,
    lng1: Double,
    lat2: Double,
    lng2: Double
): Double {
    val earthRadiusKm = 6371.0
    val dLat = Math.toRadians(lat2 - lat1)
    val dLng = Math.toRadians(lng2 - lng1)
    val a = sin(dLat / 2).pow(2) +
            cos(Math.toRadians(lat1)) *
            cos(Math.toRadians(lat2)) *
            sin(dLng / 2).pow(2)
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))
    return earthRadiusKm * c
}

private fun normalizeHeading(
    heading: Float
): Float {
    var normalized = heading % 360
    if (normalized < 0) normalized += 360
    return normalized
}


class GoogleMap : Map {

    @Composable
    override fun View() {
        val context = LocalContext.current
        val viewModel = koinViewModel<MapViewModel>()
        val state by viewModel.container.stateFlow.collectAsStateWithLifecycle()
        val camera = rememberCameraPositionState {
            getCurrentLocation(
                context = context,
                onLocationFetched = { point ->
                    moveTo(
                        point = MapPoint(point.latitude, point.longitude),
                        zoom = MapConstants.DEFAULT_ZOOM.toFloat()
                    )
                }
            )
        }
        val driversVisibility by remember(camera.position.zoom) {
            mutableStateOf(camera.position.zoom >= 8)
        }

        LaunchedEffect(camera.isMoving) {
            viewModel.onIntent(
                MyMapIntent.SetMarkerState(
                    markerState = MarkerState(
                        point = camera.position.target.let { MapPoint(it.latitude, it.longitude) },
                        isMoving = camera.isMoving,
                        isByUser = camera.cameraMoveStartedReason in listOf(
                            CameraMoveStartedReason.GESTURE,
                            CameraMoveStartedReason.DEVELOPER_ANIMATION
                        )
                    )
                )
            )
        }

        GoogleMap(
            cameraPositionState = camera,
            contentPadding = state.viewPadding,
            onMapLoaded = { viewModel.onIntent(MyMapIntent.MapReady) }
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

        viewModel.collectSideEffect { effect ->
            when (effect) {
                is MapEffect.MoveTo -> {
                    camera.moveTo(
                        point = effect.point
                    )
                }

                is MapEffect.MoveToWithZoom -> {
                    camera.moveTo(
                        point = effect.point,
                        zoom = effect.zoom.toFloat()
                    )
                }

                is MapEffect.AnimateTo -> {
                    camera.animateTo(
                        point = effect.point
                    )
                }

                is MapEffect.AnimateToWithZoom -> {
                    camera.animateTo(
                        point = effect.point,
                        zoom = effect.zoom.toFloat()
                    )
                }

                is MapEffect.AnimateToWithZoomAndDuration -> {
                    camera.animateTo(
                        point = effect.point,
                        zoom = effect.zoom.toFloat(),
                        durationMs = effect.durationMs
                    )
                }

                is MapEffect.FitBounds -> {
                    camera.fitBounds(
                        points = effect.points,
                        padding = dpToPx(
                            context = context,
                            dp = state.mapPadding
                        )
                    )
                }

                is MapEffect.AnimateFitBounds -> {
                    camera.animateFitBounds(
                        points = effect.points,
                        padding = dpToPx(context, state.mapPadding),
                        durationMs = effect.durationMs
                    )
                }

                MapEffect.ZoomOut -> {
                    if (camera.position.zoom > MapConstants.MIN_ZOOM) {
                        camera.zoomOut()
                    }
                }
            }
        }
    }
}

private fun CameraPositionState.moveTo(
    point: MapPoint,
    zoom: Float = position.zoom,
) {
    move(
        update = CameraUpdateFactory
            .newLatLngZoom(LatLng(point.lat, point.lng), zoom)
    )
}

private suspend fun CameraPositionState.animateTo(
    point: MapPoint,
    zoom: Float = position.zoom,
    durationMs: Int = MapConstants.ANIMATION_DURATION_MS,
) {
    animate(
        durationMs = durationMs,
        update = CameraUpdateFactory
            .newLatLngZoom(LatLng(point.lat, point.lng), zoom)
    )
}

private fun CameraPositionState.fitBounds(
    points: List<MapPoint>,
    padding: Int
) {
    val builder = LatLngBounds.builder()
    points.forEach { builder.include(LatLng(it.lat, it.lng)) }
    val bounds = builder.build()
    move(
        update = CameraUpdateFactory
            .newLatLngBounds(bounds, padding)
    )
}

private suspend fun CameraPositionState.animateFitBounds(
    points: List<MapPoint>,
    padding: Int,
    durationMs: Int = MapConstants.ANIMATION_DURATION_MS
) {
    val builder = LatLngBounds.builder()
    points.forEach { builder.include(LatLng(it.lat, it.lng)) }
    val bounds = builder.build()
    animate(
        durationMs = durationMs,
        update = CameraUpdateFactory
            .newLatLngBounds(bounds, padding)
    )
}

private suspend fun CameraPositionState.zoomOut(
    durationMs: Int = MapConstants.ANIMATION_DURATION_MS
) {
    animate(
        durationMs = durationMs,
        update = CameraUpdateFactory
            .zoomOut()
    )
}