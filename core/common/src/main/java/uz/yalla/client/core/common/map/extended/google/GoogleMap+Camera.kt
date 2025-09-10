package uz.yalla.client.core.common.map.extended.google

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.CameraPositionState
import uz.yalla.client.core.common.map.core.MapConstants
import uz.yalla.client.core.domain.model.MapPoint

fun CameraPositionState.moveTo(
    point: MapPoint,
    zoom: Float = position.zoom,
) {
    move(
        update = CameraUpdateFactory
            .newLatLngZoom(LatLng(point.lat, point.lng), zoom)
    )
}

suspend fun CameraPositionState.animateTo(
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

fun CameraPositionState.fitBounds(
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

suspend fun CameraPositionState.animateFitBounds(
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

suspend fun CameraPositionState.zoomOut(
    durationMs: Int = MapConstants.ANIMATION_DURATION_MS
) {
    animate(
        durationMs = durationMs,
        update = CameraUpdateFactory
            .zoomOut()
    )
}