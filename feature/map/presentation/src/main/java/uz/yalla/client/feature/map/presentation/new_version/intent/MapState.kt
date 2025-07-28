package uz.yalla.client.feature.map.presentation.new_version.intent

import androidx.compose.ui.unit.Dp
import uz.yalla.client.core.common.marker.YallaMarkerState
import uz.yalla.client.core.common.state.CameraButtonState
import uz.yalla.client.core.common.state.NavigationButtonState
import uz.yalla.client.core.domain.model.Client
import uz.yalla.client.core.domain.model.Destination
import uz.yalla.client.core.domain.model.Location
import uz.yalla.client.core.domain.model.MapPoint
import uz.yalla.client.feature.order.domain.model.response.order.ShowOrderModel

data class MapState(
    val client: Client? = null,
    val balance: Long = 0,
    val notificationCount: Int? = null,

    val isMapEnabled: Boolean = true,
    val overlayPadding: Dp = Dp.Unspecified,
    val sheetHeight: Dp = Dp.Unspecified,

    val serviceAvailable: Boolean = true,
    val location: Location? = null,
    val destinations: List<Destination> = emptyList(),
    val route: List<MapPoint> = emptyList(),
    val tariffId: Int? = null,

    val markerState: YallaMarkerState = YallaMarkerState.LOADING,
    val carArrivalInMinutes: Int? = null,
    val orderEndsInMinutes: Int? = null,

    val orderId: Int? = null,
    val order: ShowOrderModel? = null,
    val orders: List<ShowOrderModel> = emptyList(),

    val navigationButtonState: NavigationButtonState = NavigationButtonState.OpenDrawer,
    val cameraButtonState: CameraButtonState = CameraButtonState.MyLocationView,

    val locationEnabled: Boolean = true,
    val locationGranted: Boolean = true,

    val ordersSheetVisible: Boolean = false,
    val permissionDialogVisible: Boolean = false
)