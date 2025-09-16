package uz.yalla.client.core.common.map.static.intent

import uz.yalla.client.core.domain.model.MapPoint

sealed interface StaticMapEffect {
    data object MoveToFitBounds : StaticMapEffect
}