package uz.yalla.client.core.common.new_map.libre

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.sargunv.maplibrecompose.compose.CameraState
import dev.sargunv.maplibrecompose.compose.MaplibreMap
import dev.sargunv.maplibrecompose.compose.layer.LineLayer
import dev.sargunv.maplibrecompose.compose.layer.SymbolLayer
import dev.sargunv.maplibrecompose.compose.rememberCameraState
import dev.sargunv.maplibrecompose.compose.source.rememberGeoJsonSource
import dev.sargunv.maplibrecompose.core.CameraPosition
import dev.sargunv.maplibrecompose.expressions.dsl.const
import dev.sargunv.maplibrecompose.expressions.dsl.image
import dev.sargunv.maplibrecompose.expressions.value.LineCap
import dev.sargunv.maplibrecompose.expressions.value.LineJoin
import io.github.dellisd.spatialk.geojson.BoundingBox
import io.github.dellisd.spatialk.geojson.Feature
import io.github.dellisd.spatialk.geojson.FeatureCollection
import io.github.dellisd.spatialk.geojson.LineString
import io.github.dellisd.spatialk.geojson.Point
import io.github.dellisd.spatialk.geojson.Position
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import org.orbitmvi.orbit.compose.collectSideEffect
import uz.yalla.client.core.common.R
import uz.yalla.client.core.common.marker.rememberLibreMarkerWithInfo
import uz.yalla.client.core.common.new_map.core.Map
import uz.yalla.client.core.common.new_map.core.MapConstants
import uz.yalla.client.core.common.new_map.core.MapEffect
import uz.yalla.client.core.common.new_map.core.MapViewModel
import uz.yalla.client.core.domain.local.AppPreferences
import uz.yalla.client.core.domain.model.Executor
import uz.yalla.client.core.domain.model.MapPoint
import uz.yalla.client.core.domain.model.OrderStatus
import uz.yalla.client.core.presentation.design.theme.YallaTheme
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.time.Duration.Companion.milliseconds

@Composable
private fun Markers(
    route: List<MapPoint>,
    orderStatus: OrderStatus?,
    locations: List<MapPoint>,
    carArrivesInMinutes: Int? = null,
    orderEndsInMinutes: Int? = null
) {
    val isDark = isSystemInDarkTheme()

    if (route.isNotEmpty()) {
        PolylineLayer(
            id = "route",
            coordinates = route,
            color = if (isDark) Color.White else Color.Black,
            widthDp = 4f
        )
    }

    if (route.isNotEmpty() && orderStatus !in OrderStatus.cancellable) {
        locations.forEachIndexed { index, location ->
            val target: MapPoint? = when (index) {
                0 -> route.firstOrNull()
                locations.lastIndex -> route.lastOrNull()
                else -> uz.yalla.client.core.common.utils.findClosestPointOnRoute(location, route)
            }
            if (target != null && target != location) {
                PolylineLayer(
                    id = "conn_$index",
                    coordinates = listOf(location, target),
                    color = YallaTheme.color.gray,
                    widthDp = 2f
                )
            }
        }
    }

    if (locations.isEmpty()) return

    val originIcon = rememberLibreMarkerWithInfo(
        key = "origin_${locations.firstOrNull()?.hashCode()}",
        title = carArrivesInMinutes?.let { stringResource(R.string.x_min, it.toString()) },
        description = stringResource(R.string.coming),
        infoColor = YallaTheme.color.primary,
        pointColor = YallaTheme.color.gray,
        pointBackgroundColor = YallaTheme.color.background
    )

    val middleIcon = painterResource(R.drawable.ic_middle_marker)

    val endIcon = rememberLibreMarkerWithInfo(
        key = "destination_${locations.lastOrNull()?.hashCode()}",
        title = orderEndsInMinutes?.let { stringResource(R.string.x_min, it.toString()) },
        description = stringResource(R.string.on_the_way),
        infoColor = YallaTheme.color.onBackground,
        pointColor = YallaTheme.color.primary,
        pointBackgroundColor = YallaTheme.color.background
    )

    if ((orderStatus != null && orderStatus in OrderStatus.nonInteractive) || route.isNotEmpty()) {
        locations.firstOrNull()?.let { start ->
            originIcon?.let { MarkerLayer("start", start, it) }
        }
    }

    if (locations.size > 2) {
        locations.subList(1, locations.lastIndex).forEachIndexed { idx, mid ->
            MarkerLayer("middle_$idx", mid, middleIcon)
        }
    }

    if (locations.size > 1) {
        locations.lastOrNull()?.let { end ->
            endIcon?.let { MarkerLayer("end", end, it) }
        }
    }
}

@Composable
private fun PolylineLayer(
    id: String,
    coordinates: List<MapPoint>,
    color: Color,
    widthDp: Float
) {
    val geo = FeatureCollection(
        Feature(LineString(coordinates.map {
            Position(longitude = it.lng, latitude = it.lat)
        }))
    )
    val src = rememberGeoJsonSource(id = "$id-src", data = geo)
    LineLayer(
        id = id,
        source = src,
        color = const(color),
        width = const(widthDp.dp),
        cap = const(LineCap.Round),
        join = const(LineJoin.Round)
    )
}

@Composable
private fun MarkerLayer(id: String, point: MapPoint, painter: Painter) {
    val src = rememberGeoJsonSource(
        id = id,
        data = Feature(Point(Position(longitude = point.lng, latitude = point.lat)))
    )
    SymbolLayer(
        id = id,
        source = src,
        iconImage = image(painter),
        iconAllowOverlap = const(true),
        iconIgnorePlacement = const(true)
    )
}

@Composable
private fun DriverLibre(driver: Executor?) {
    if (driver == null) return
    val painter = painterResource(R.drawable.img_car_marker)

    val src = rememberGeoJsonSource(
        id = "driver-src",
        data = Feature(Point(Position(longitude = driver.lng, latitude = driver.lat)))
    )

    SymbolLayer(
        id = "driver",
        source = src,
        iconImage = image(painter),
        iconAllowOverlap = const(true),
        iconIgnorePlacement = const(true),
        iconRotate = const(driver.heading.toFloat())
    )
}

@Composable
private fun DriversWithAnimationLibre(drivers: List<Executor>) {
    drivers.take(20).forEach { drv ->
        key(drv.id) {
            val lat = remember { Animatable(drv.lat.toFloat()) }
            val lng = remember { Animatable(drv.lng.toFloat()) }
            val heading = remember { Animatable(drv.heading.toFloat()) }
            val busy = remember { mutableStateOf(false) }

            LaunchedEffect(drv.lat, drv.lng, drv.heading) {
                if (!busy.value) {
                    busy.value = true
                    val d = haversine(lat.value.toDouble(), lng.value.toDouble(), drv.lat, drv.lng)
                    val duration = when {
                        d < 0.0005 -> 300
                        d < 0.002 -> 800
                        d < 0.01 -> 1200
                        else -> 1800
                    }
                    launch {
                        lat.animateTo(
                            drv.lat.toFloat(),
                            tween(duration, easing = FastOutSlowInEasing)
                        )
                    }
                    launch {
                        lng.animateTo(
                            drv.lng.toFloat(),
                            tween(duration, easing = FastOutSlowInEasing)
                        )
                    }
                    launch {
                        val target = normalizeHeading(drv.heading.toFloat())
                        heading.animateTo(
                            target,
                            tween((duration * 1.1).toInt(), easing = FastOutSlowInEasing)
                        )
                    }
                    busy.value = false
                }
            }

            val src = rememberGeoJsonSource(
                id = "drv-${drv.id}-src",
                data = Feature(
                    Point(
                        Position(
                            longitude = lng.value.toDouble(),
                            latitude = lat.value.toDouble()
                        )
                    )
                )
            )
            SymbolLayer(
                id = "drv-${drv.id}-lyr",
                source = src,
                iconImage = image(painterResource(R.drawable.img_car_marker)),
                iconAllowOverlap = const(true),
                iconIgnorePlacement = const(true),
                iconRotate = const(heading.value)
            )
        }
    }
}

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

private suspend fun CameraState.moveTo(
    point: MapPoint,
    padding: PaddingValues,
    zoom: Double = position.zoom
) {
    animateTo(
        duration = 1.milliseconds,
        finalPosition = CameraPosition(
            target = Position(longitude = point.lng, latitude = point.lat),
            zoom = zoom,
            padding = padding
        )
    )
}

private suspend fun CameraState.animateTo(
    point: MapPoint,
    padding: PaddingValues,
    zoom: Double = position.zoom,
    durationMs: Int = MapConstants.ANIMATION_DURATION_MS
) {
    animateTo(
        duration = durationMs.milliseconds,
        finalPosition = CameraPosition(
            target = Position(longitude = point.lng, latitude = point.lat),
            zoom = zoom,
            padding = padding
        )
    )
}

private suspend fun CameraState.fitBounds(
    points: List<MapPoint>,
    padding: PaddingValues
) {
    animateTo(
        duration = 1.milliseconds,
        boundingBox = points.toBoundingBox(),
        padding = padding
    )
}

private suspend fun CameraState.animateFitBounds(
    points: List<MapPoint>,
    padding: PaddingValues,
    durationMs: Int = MapConstants.ANIMATION_DURATION_MS
) {
    animateTo(
        duration = durationMs.milliseconds,
        boundingBox = points.toBoundingBox(),
        padding = padding
    )
}

private suspend fun CameraState.zoomOut(
    durationMs: Int = MapConstants.ANIMATION_DURATION_MS
) {
    animateTo(
        duration = durationMs.milliseconds,
        finalPosition = CameraPosition(
            bearing = position.bearing,
            target = position.target,
            zoom = position.zoom - 1,
            padding = position.padding
        )
    )
}

private fun List<MapPoint>.toBoundingBox(): BoundingBox {
    if (isEmpty()) return BoundingBox(emptyList())
    val minLat = minOf { it.lat }
    val maxLat = maxOf { it.lat }
    val minLng = minOf { it.lng }
    val maxLng = maxOf { it.lng }
    return BoundingBox(listOf(minLng, minLat, maxLng, maxLat))
}

private fun haversine(lat1: Double, lng1: Double, lat2: Double, lng2: Double): Double {
    val r = 6371.0
    val dLat = Math.toRadians(lat2 - lat1)
    val dLng = Math.toRadians(lng2 - lng1)
    val a = sin(dLat / 2).pow(2.0) +
            cos(Math.toRadians(lat1)) *
            cos(Math.toRadians(lat2)) *
            sin(dLng / 2).pow(2.0)
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))
    return r * c
}

private fun normalizeHeading(heading: Float): Float {
    var n = heading % 360
    if (n < 0) n += 360
    return n
}
