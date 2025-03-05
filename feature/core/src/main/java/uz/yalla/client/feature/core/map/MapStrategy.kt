package uz.yalla.client.feature.core.map

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import uz.yalla.client.core.domain.model.Executor
import uz.yalla.client.core.domain.model.MapPoint

interface MapStrategy {
    val mapPoint: MutableState<MapPoint>
    val isMarkerMoving: State<Boolean>

    @Composable
    fun Map(
        startingPoint: MapPoint?,
        modifier: Modifier,
        contentPadding: PaddingValues
    )

    fun move(to: MapPoint)
    fun animate(to: MapPoint, durationMillis: Int = 1000)
    fun moveToMyLocation()
    fun animateToMyLocation(durationMillis: Int = 1000)
    fun moveToFitBounds(routing: List<MapPoint>)
    fun animateToFitBounds(routing: List<MapPoint>)
    fun updateDriver(driver: ShowOrderModel.Executor)
    fun updateDrivers(drivers: List<Executor>)
    fun updateRoute(route: List<MapPoint>)
    fun updateOrderStatus(status: OrderStatus)
    fun updateLocations(locations: List<MapPoint>)
}