package uz.yalla.client.feature.map.presentation.model

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import uz.yalla.client.core.common.marker.YallaMarkerState
import uz.yalla.client.core.common.state.CameraButtonState
import uz.yalla.client.core.domain.model.Destination
import uz.yalla.client.core.domain.model.Executor
import uz.yalla.client.core.domain.model.MapPoint
import uz.yalla.client.core.domain.model.Location
import uz.yalla.client.feature.order.domain.model.response.order.ShowOrderModel
import uz.yalla.client.feature.profile.domain.model.response.GetMeModel

data class MapUIState(
    val location: Location? = null,
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

    val drivers: List<Executor> = emptyList(),

    val sheetHeight: Dp = 0.dp,
    val overlayPadding: Dp = 0.dp,
    val cameraButtonState: CameraButtonState = CameraButtonState.MyLocationView,
    val loading: Boolean = false,
    val markerState: YallaMarkerState = YallaMarkerState.LOADING,
    val isActiveOrdersSheetVisibility: Boolean = false,
    val notificationsCount: Int = 0,
    val bonusAmount: Int = 0
)
