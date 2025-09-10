package uz.yalla.client.core.common.map.core

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.unit.LayoutDirection
import io.github.dellisd.spatialk.geojson.BoundingBox
import uz.yalla.client.core.domain.model.MapPoint
import kotlin.math.*

infix operator fun PaddingValues.plus(other: PaddingValues): PaddingValues {
    return PaddingValues(
        top = calculateTopPadding()
                + other.calculateTopPadding(),
        bottom = calculateBottomPadding()
                + other.calculateBottomPadding(),
        start = calculateLeftPadding(LayoutDirection.Ltr)
                + other.calculateLeftPadding(LayoutDirection.Ltr),
        end = calculateRightPadding(LayoutDirection.Ltr)
                + other.calculateRightPadding(LayoutDirection.Ltr)
    )
}

fun calculateDistance(
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

fun normalizeHeading(
    heading: Float
): Float {
    var normalized = heading % 360
    if (normalized < 0) normalized += 360
    return normalized
}

fun List<MapPoint>.toBoundingBox(): BoundingBox {
    if (isEmpty()) return BoundingBox(emptyList())
    val minLat = minOf { it.lat }
    val maxLat = maxOf { it.lat }
    val minLng = minOf { it.lng }
    val maxLng = maxOf { it.lng }
    return BoundingBox(listOf(minLng, minLat, maxLng, maxLat))
}

fun haversine(lat1: Double, lng1: Double, lat2: Double, lng2: Double): Double {
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