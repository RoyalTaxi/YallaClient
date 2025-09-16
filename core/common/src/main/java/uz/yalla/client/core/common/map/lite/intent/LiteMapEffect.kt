package uz.yalla.client.core.common.map.lite.intent

import uz.yalla.client.core.domain.model.MapPoint

sealed interface LiteMapEffect {
    data class MoveTo(val point: MapPoint) : LiteMapEffect
    data class AnimateTo(val point: MapPoint) : LiteMapEffect
}