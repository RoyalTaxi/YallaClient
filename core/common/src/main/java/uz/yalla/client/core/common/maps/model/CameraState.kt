package uz.yalla.client.core.common.maps.model

import uz.yalla.client.core.domain.model.MapPoint

data class CameraState(
    val position: MapPoint = MapPoint(0.0, 0.0),
    val isMoving: Boolean = false,
    val isByUser: Boolean = false
)