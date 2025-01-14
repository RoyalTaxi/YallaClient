package uz.yalla.client.feature.core.map

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import uz.ildam.technologies.yalla.core.domain.model.MapPoint

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