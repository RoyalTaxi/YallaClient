package uz.yalla.client.core.common.map.lite.intent

import uz.yalla.client.core.common.map.core.MarkerState
import uz.yalla.client.core.domain.model.Location

sealed interface LiteMapIntent {
    data object MapReady : LiteMapIntent
    data class SetMarkerState(val markerState: MarkerState) : LiteMapIntent
    data class SetMapPadding(val padding: Int) : LiteMapIntent
    data class SetLocation(val location: Location?) : LiteMapIntent
    data object MoveToMyLocation : LiteMapIntent
    data object AnimateToMyLocation : LiteMapIntent
}