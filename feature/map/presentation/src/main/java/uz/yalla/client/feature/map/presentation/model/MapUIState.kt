package uz.yalla.client.feature.map.presentation.model

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import uz.yalla.client.core.common.marker.YallaMarkerState
import uz.yalla.client.core.common.state.MoveCameraButtonState
import uz.yalla.client.core.domain.model.Destination
import uz.yalla.client.core.domain.model.Executor
import uz.yalla.client.core.domain.model.MapPoint
import uz.yalla.client.core.domain.model.SelectedLocation
import uz.yalla.client.feature.order.domain.model.response.order.ShowOrderModel
import uz.yalla.client.feature.profile.domain.model.response.GetMeModel

data class MapUIState(
    val selectedLocation: SelectedLocation? = null,
    val destinations: List<Destination> = emptyList(),

    val route: List<MapPoint> = emptyList(),
    val driverRoute: List<MapPoint> = emptyList(),
    val hasServiceProvided: Boolean? = null,
    val timeout: Int? = null,
    val orderEndsInMinutes: Int? = null,
    val selectedTariffId: Int? = null,

    val orders: List<ShowOrderModel> = emptyList(),
    val selectedOrder: ShowOrderModel? = null,
    val user: GetMeModel? = null,

    val lastHandledServiceState: Boolean? = null,

    val drivers: List<Executor> = emptyList(),

    val sheetHeight: Dp = 0.dp,
    val moveCameraButtonState: MoveCameraButtonState = MoveCameraButtonState.MyLocationView,
    val loading: Boolean = false,
    val isMarkerVisible: Boolean = true,
    val markerState: YallaMarkerState = YallaMarkerState.LOADING,
    val isActiveOrdersSheetVisibility: Boolean = false,
    val notificationsCount: Int = 0,
    val bonusAmount: Int = 0,
    val hasProcessedOrderOnEntry: Boolean = true
)
