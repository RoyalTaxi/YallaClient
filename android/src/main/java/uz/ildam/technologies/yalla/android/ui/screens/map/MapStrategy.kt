package uz.ildam.technologies.yalla.android.ui.screens.map

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import uz.ildam.technologies.yalla.core.domain.model.MapPoint

interface MapStrategy {
    val isMarkerMoving: State<Boolean>
    val mapPoint: MutableState<MapPoint>

    @Composable
    fun Map(
        modifier: Modifier,
        uiState: MapUIState
    )

    fun move(to: MapPoint)
    fun animate(to: MapPoint, durationMillis: Int = 1000)
    fun moveToMyLocation()
    fun animateToMyLocation(durationMillis: Int = 1000)
    fun moveToFitBounds(routing: List<MapPoint>)
    fun animateToFitBounds(routing: List<MapPoint>)
}