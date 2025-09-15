package uz.yalla.client.core.common.map.lite.intent

import androidx.compose.foundation.layout.PaddingValues
import uz.yalla.client.core.common.map.extended.intent.MarkerState
import uz.yalla.client.core.domain.model.Location

sealed interface LiteMapIntent {
    data object MapReady : LiteMapIntent
    data class SetMarkerState(val markerState: MarkerState) : LiteMapIntent
    data class SetMapPadding(val padding: Int) : LiteMapIntent
    data class SetViewPadding(val padding: PaddingValues) : LiteMapIntent
    data class SetLocation(val location: Location?) : LiteMapIntent
    data object MoveToMyLocation : LiteMapIntent
    data object AnimateToMyLocation : LiteMapIntent
}