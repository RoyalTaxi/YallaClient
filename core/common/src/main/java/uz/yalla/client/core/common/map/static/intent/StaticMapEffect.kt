package uz.yalla.client.core.common.map.static.intent

sealed interface StaticMapEffect {
    data object MoveToFitBounds : StaticMapEffect
}