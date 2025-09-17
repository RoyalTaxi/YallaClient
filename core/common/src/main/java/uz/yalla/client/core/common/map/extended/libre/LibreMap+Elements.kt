package uz.yalla.client.core.common.map.extended.libre

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.github.dellisd.spatialk.geojson.dsl.featureCollection
import io.github.dellisd.spatialk.geojson.dsl.lineString
import io.github.dellisd.spatialk.geojson.dsl.point
import kotlinx.coroutines.launch
import org.maplibre.compose.expressions.dsl.const
import org.maplibre.compose.expressions.dsl.image
import org.maplibre.compose.expressions.value.LineCap
import org.maplibre.compose.expressions.value.LineJoin
import org.maplibre.compose.expressions.value.SymbolAnchor
import org.maplibre.compose.layers.LineLayer
import org.maplibre.compose.layers.SymbolLayer
import org.maplibre.compose.sources.GeoJsonData
import org.maplibre.compose.sources.rememberGeoJsonSource
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
fun LibreMarkerLayer(
    id: String,
    point: MapPoint,
    painter: Painter,
    anchor: SymbolAnchor = SymbolAnchor.Center
) {
    val src = rememberGeoJsonSource(
        data = GeoJsonData.Features(
            featureCollection {
                feature(geometry = point(longitude = point.lng, latitude = point.lat))
            }
        )
    )

    SymbolLayer(
        id = id,
        source = src,
        iconImage = image(painter),
        iconAnchor = const(anchor),
        iconAllowOverlap = const(true),
        iconIgnorePlacement = const(true)
    )
}

@Composable
fun LibrePolylineLayer(
    id: String,
    coordinates: List<MapPoint>,
    color: Color,
    widthDp: Float,
    pattern: List<Float>? = null
) {
    val src = rememberGeoJsonSource(
        data = GeoJsonData.Features(
            featureCollection {
                feature(
                    geometry = lineString {
                        coordinates.forEach { mp ->
                            point(longitude = mp.lng, latitude = mp.lat)
                        }
                    }
                )
            }
        )
    )

    if (pattern == null) {
        LineLayer(
            id = id,
            source = src,
            color = const(color),
            width = const(widthDp.dp),
            cap = const(LineCap.Round),
            join = const(LineJoin.Round)
        )
    } else {
        val dashValues = with(LocalDensity.current) { pattern.map { it.dp.toPx().toDouble() } }
        LineLayer(
            id = id,
            source = src,
            color = const(color),
            width = const(widthDp.dp),
            cap = const(LineCap.Round),
            join = const(LineJoin.Round),
            dasharray = const(dashValues)
        )
    }
}

@Composable
fun LibreMarkers(
    isSystemInDark: Boolean,
    route: List<MapPoint>,
    orderStatus: OrderStatus?,
    locations: List<MapPoint>,
    carArrivesInMinutes: Int? = null,
    orderEndsInMinutes: Int? = null
) {
    if (route.isNotEmpty()) {
        key("route") {
            LibrePolylineLayer(
                id = "route",
                coordinates = route,
                color = if (isSystemInDark) Color.White else Color.Black,
                widthDp = 4f
            )
        }
    }

    if (route.isNotEmpty() && orderStatus !in OrderStatus.cancellable) {
        locations.forEachIndexed { index, location ->
            val target: MapPoint? = when (index) {
                0 -> route.firstOrNull()
                locations.lastIndex -> route.lastOrNull()
                else -> findClosestPointOnRoute(location, route)
            }
            if (target != null && target != location) {
                key("conn_$index") {
                    LibrePolylineLayer(
                        id = "conn_$index",
                        coordinates = listOf(location, target),
                        color = YallaTheme.color.gray,
                        widthDp = 2f,
                        pattern = listOf(16f, 16f)
                    )
                }
            }
        }
    }

    if (locations.isEmpty()) return

    val originIcon = key(
        locations.firstOrNull()?.hashCode()?.plus(carArrivesInMinutes.hashCode())
    ) {
        rememberLibreMarkerWithInfo(
            key = "origin_${locations.firstOrNull()?.hashCode()}_${carArrivesInMinutes}",
            title = carArrivesInMinutes?.let { stringResource(R.string.x_min, it.toString()) },
            description = stringResource(R.string.coming),
            infoColor = YallaTheme.color.primary,
            pointColor = YallaTheme.color.gray,
            pointBackgroundColor = YallaTheme.color.background
        )
    }

    val middleIcon = painterResource(R.drawable.ic_middle_marker)

    val endIcon = key(
        locations.lastOrNull()?.hashCode()?.plus(orderEndsInMinutes.hashCode())
    ) {
        rememberLibreMarkerWithInfo(
            key = "destination_${locations.lastOrNull()?.hashCode()}_${orderEndsInMinutes}",
            title = orderEndsInMinutes?.let { stringResource(R.string.x_min, it.toString()) },
            description = stringResource(R.string.on_the_way),
            infoColor = YallaTheme.color.black,
            pointColor = YallaTheme.color.primary,
            pointBackgroundColor = YallaTheme.color.background
        )
    }

    if ((orderStatus != null && orderStatus in OrderStatus.nonInteractive) || route.isNotEmpty() || orderStatus == OrderStatus.Completed) {
        locations.firstOrNull()?.let { start ->
            originIcon?.let {
                key("start-${start.hashCode()}") {
                    LibreMarkerLayer(
                        id = "start",
                        point = start,
                        painter = it,
                        anchor = SymbolAnchor.Bottom
                    )
                }
            }
        }
    }

    if (locations.size > 2) {
        locations.subList(1, locations.lastIndex).forEachIndexed { idx, mid ->
            key("middle_${idx}-${mid.hashCode()}") {
                LibreMarkerLayer(
                    id = "middle_$idx",
                    point = mid,
                    painter = middleIcon
                )
            }
        }
    }

    if (locations.size > 1) {
        locations.lastOrNull()?.let { end ->
            endIcon?.let {
                LibreMarkerLayer(
                    id = "end",
                    point = end,
                    painter = it,
                    anchor = SymbolAnchor.Bottom
                )
            }
        }
    }
}

@Composable
fun DriverLibre(driver: Executor?) {
    if (driver == null) return
    val painter = painterResource(R.drawable.img_car_marker)

    val src = rememberGeoJsonSource(
        data = GeoJsonData.Features(
            featureCollection {
                feature(geometry = point(longitude = driver.lng, latitude = driver.lat))
            }
        )
    )

    SymbolLayer(
        id = "driver",
        source = src,
        iconImage = image(painter),
        iconAllowOverlap = const(true),
        iconIgnorePlacement = const(true),
        iconRotate = const(driver.heading.toFloat()),
        iconSize = const(.5f)
    )
}

@Composable
fun DriversWithAnimationLibre(drivers: List<Executor>) {
    val visibleDrivers = drivers.distinctBy { it.id }.take(10)
    val carPainter = painterResource(R.drawable.img_car_marker)

    visibleDrivers.forEach { drv ->
        val animatedLat = remember(drv.id) { Animatable(drv.lat.toFloat()) }
        val animatedLng = remember(drv.id) { Animatable(drv.lng.toFloat()) }
        val animatedHeading = remember(drv.id) { Animatable(drv.heading.toFloat()) }

        LaunchedEffect(drv.lat, drv.lng, drv.heading) {
            val distance = haversine(
                animatedLat.value.toDouble(),
                animatedLng.value.toDouble(),
                drv.lat,
                drv.lng
            )

            val duration = when {
                distance < 0.0005 -> 300
                distance < 0.002 -> 800
                distance < 0.01 -> 1200
                else -> 1800
            }

            launch {
                animatedLat.animateTo(
                    targetValue = drv.lat.toFloat(),
                    animationSpec = tween(duration, easing = FastOutSlowInEasing)
                )
            }
            launch {
                animatedLng.animateTo(
                    targetValue = drv.lng.toFloat(),
                    animationSpec = tween(duration, easing = FastOutSlowInEasing)
                )
            }
            launch {
                val targetHeading = normalizeHeading(drv.heading.toFloat())
                animatedHeading.animateTo(
                    targetValue = targetHeading,
                    animationSpec = tween((duration * 1.1).toInt(), easing = FastOutSlowInEasing)
                )
            }
        }

        val src = rememberGeoJsonSource(
            data = GeoJsonData.Features(
                featureCollection {
                    feature(geometry = point(longitude = animatedLng.value.toDouble(), latitude = animatedLat.value.toDouble()))
                }
            )
        )

        SymbolLayer(
            id = "drv-${drv.id}-lyr",
            source = src,
            iconImage = image(carPainter),
            iconAllowOverlap = const(true),
            iconIgnorePlacement = const(true),
            iconRotate = const(animatedHeading.value),
            iconSize = const(.5f)
        )
    }
}