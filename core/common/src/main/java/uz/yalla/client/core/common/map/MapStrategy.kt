package uz.yalla.client.core.common.map

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import kotlinx.coroutines.flow.Flow
import uz.yalla.client.core.domain.model.Executor
import uz.yalla.client.core.domain.model.MapPoint
import uz.yalla.client.core.domain.model.OrderStatus
import uz.yalla.client.feature.order.domain.model.response.order.ShowOrderModel

interface MapStrategy {
    val mapPoint: MutableState<MapPoint>
    val isMarkerMoving: Flow<Pair<Boolean, Boolean>>

    @Composable
    fun Map(
        startingPoint: MapPoint?,
        enabled: Boolean,
        modifier: Modifier,
        contentPadding: PaddingValues,
        onMapReady: () -> Unit
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
    fun updateOrderStatus(status: OrderStatus?)
    fun updateLocations(locations: List<MapPoint>)
    fun zoomOut()
}