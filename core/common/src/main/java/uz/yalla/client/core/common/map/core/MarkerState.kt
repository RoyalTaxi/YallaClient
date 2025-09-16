package uz.yalla.client.core.common.map.core

import uz.yalla.client.core.domain.model.MapPoint

data class MarkerState(
    val point: MapPoint,
    val isMoving: Boolean,
    val isByUser: Boolean,
) {
    companion object {
        val INITIAL = MarkerState(
            point = MapPoint.Zero,
            isMoving = false,
            isByUser = false,
        )
    }
}