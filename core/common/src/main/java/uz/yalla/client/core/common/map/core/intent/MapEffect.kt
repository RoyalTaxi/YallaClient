package uz.yalla.client.core.common.map.core.intent

import uz.yalla.client.core.domain.model.MapPoint

sealed interface MapEffect {
    data class MoveTo(
        val point: MapPoint
    ) : MapEffect

    data class MoveToWithZoom(
        val point: MapPoint,
        val zoom: Int
    ) : MapEffect

    data class AnimateTo(
        val point: MapPoint
    ) : MapEffect

    data class AnimateToWithZoom(
        val point: MapPoint,
        val zoom: Int,
    ) : MapEffect

    data class AnimateToWithZoomAndDuration(
        val point: MapPoint,
        val zoom: Int,
        val durationMs: Int
    ) : MapEffect

    data class FitBounds(
        val points: List<MapPoint>,
    ) : MapEffect

    data class AnimateFitBounds(
        val points: List<MapPoint>,
        val durationMs: Int
    ) : MapEffect

    data object ZoomOut : MapEffect
}