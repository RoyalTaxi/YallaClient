package uz.yalla.client.core.common.map.extended.libre

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.sargunv.maplibrecompose.compose.layer.LineLayer
import dev.sargunv.maplibrecompose.compose.layer.SymbolLayer
import dev.sargunv.maplibrecompose.compose.source.rememberGeoJsonSource
import dev.sargunv.maplibrecompose.expressions.dsl.const
import dev.sargunv.maplibrecompose.expressions.dsl.image
import dev.sargunv.maplibrecompose.expressions.value.LineCap
import dev.sargunv.maplibrecompose.expressions.value.LineJoin
import io.github.dellisd.spatialk.geojson.*
import kotlinx.coroutines.launch
import uz.yalla.client.core.common.R
import uz.yalla.client.core.common.map.core.haversine
import uz.yalla.client.core.common.map.core.normalizeHeading
import uz.yalla.client.core.common.marker.rememberLibreMarkerWithInfo
import uz.yalla.client.core.common.utils.findClosestPointOnRoute
import uz.yalla.client.core.domain.model.Executor
import uz.yalla.client.core.domain.model.MapPoint
import uz.yalla.client.core.domain.model.OrderStatus
import uz.yalla.client.core.presentation.design.theme.YallaTheme


@Composable
fun Markers(
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
                else -> findClosestPointOnRoute(location, route)
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
fun PolylineLayer(
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
fun MarkerLayer(id: String, point: MapPoint, painter: Painter) {
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
fun DriverLibre(driver: Executor?) {
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
fun DriversWithAnimationLibre(drivers: List<Executor>) {
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