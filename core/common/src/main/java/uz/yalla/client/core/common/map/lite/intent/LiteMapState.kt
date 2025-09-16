package uz.yalla.client.core.common.map.lite.intent

import androidx.compose.foundation.layout.PaddingValues
import uz.yalla.client.core.common.map.core.MarkerState
import uz.yalla.client.core.domain.model.Location

data class LiteMapState(
    val markerState: MarkerState,
    val viewPadding: PaddingValues, // modifier padding of map
    val mapPadding: Int, // content padding of map
    val location: Location?,
    val isMapReady: Boolean
) {
    companion object {
        val INITIAL = LiteMapState(
            markerState = MarkerState.INITIAL,
            viewPadding = PaddingValues(),
            mapPadding = 110,
            location = null,
            isMapReady = false
        )
    }
}