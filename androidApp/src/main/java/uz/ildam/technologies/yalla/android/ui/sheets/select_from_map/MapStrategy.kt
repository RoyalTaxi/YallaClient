package uz.ildam.technologies.yalla.android.ui.sheets.select_from_map

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier

interface MapStrategy {
    val isMarkerMoving: State<Boolean>
    val mapPoint: MutableState<MapPoint>

    @Composable
    fun Map(modifier: Modifier)
    fun move(to: MapPoint)
    fun animate(to: MapPoint, durationMillis: Int = 1000)
    fun moveToMyLocation()
    fun animateToMyLocation(durationMillis: Int = 1000)
}