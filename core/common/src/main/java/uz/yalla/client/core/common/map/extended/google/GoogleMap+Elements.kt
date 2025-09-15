package uz.yalla.client.core.common.map.extended.google

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.Dash
import com.google.android.gms.maps.model.Gap
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerComposable
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberMarkerState
import kotlinx.coroutines.launch
import uz.yalla.client.core.common.R
import uz.yalla.client.core.common.marker.rememberGoogleMarkerWithInfo
import uz.yalla.client.core.common.map.core.calculateDistance
import uz.yalla.client.core.common.map.core.normalizeHeading
import uz.yalla.client.core.common.utils.findClosestPointOnRoute
import uz.yalla.client.core.common.utils.vectorToBitmapDescriptor
import uz.yalla.client.core.domain.model.Executor
import uz.yalla.client.core.domain.model.MapPoint
import uz.yalla.client.core.domain.model.OrderStatus
import uz.yalla.client.core.presentation.design.theme.YallaTheme

@Composable
fun GoogleMarkers(
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
            color = if (isDarkTheme) Color.White else Color.Black,
            clickable = false,
            zIndex = 0f
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
                    pattern = listOf(Dash(16f), Gap(16f)),
                    clickable = false,
                    zIndex = 0f
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
                zIndex = 2f,
                onClick = { true }
            )
        }
    }

    if (locations.size > 2) {
        locations.subList(1, locations.lastIndex).forEachIndexed { index, mid ->
            key(mid.hashCode(), index) {
                Marker(
                    state = rememberMarkerState(position = LatLng(mid.lat, mid.lng)),
                    icon = middleIcon,
                    zIndex = 2f,
                    onClick = { true }
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
                zIndex = 2f,
                onClick = { true }
            )
        }
    }
}

@Composable
fun GoogleDriver(
    driver: Executor?
) {
    driver?.let {
        MarkerComposable(
            flat = true,
            rotation = it.heading.toFloat(),
            state = rememberMarkerState(position = LatLng(it.lat, it.lng)),
            anchor = Offset(0.5f, 0.5f),
            zIndex = 1f, // middle layer
            onClick = { true }
        ) {
            Icon(
                painter = painterResource(R.drawable.img_car_marker),
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier.size(36.dp)
            )
        }
    }
}

@Composable
fun GoogleDriversWithAnimation(
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
                zIndex = 1f,
                onClick = { true }
            ) {
                Icon(
                    painter = painterResource(R.drawable.img_car_marker),
                    contentDescription = "Driver",
                    tint = Color.Unspecified,
                    modifier = Modifier.size(36.dp)
                )
            }
        }
    }
}