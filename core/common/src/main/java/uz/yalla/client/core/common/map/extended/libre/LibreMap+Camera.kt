package uz.yalla.client.core.common.map.extended.libre

import androidx.compose.foundation.layout.PaddingValues
import dev.sargunv.maplibrecompose.compose.CameraState
import dev.sargunv.maplibrecompose.core.CameraPosition
import io.github.dellisd.spatialk.geojson.Position
import uz.yalla.client.core.common.map.core.MapConstants
import uz.yalla.client.core.common.map.core.toBoundingBox
import uz.yalla.client.core.domain.model.MapPoint
import kotlin.time.Duration.Companion.milliseconds

suspend fun CameraState.moveTo(
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

suspend fun CameraState.animateTo(
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

suspend fun CameraState.fitBounds(
    points: List<MapPoint>,
    padding: PaddingValues
) {
    animateTo(
        duration = 1.milliseconds,
        boundingBox = points.toBoundingBox(),
        padding = padding
    )
}

suspend fun CameraState.animateFitBounds(
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

suspend fun CameraState.zoomOut(
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