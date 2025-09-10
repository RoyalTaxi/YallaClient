package uz.yalla.client.feature.home.presentation.intent

import androidx.compose.ui.unit.Dp
import uz.yalla.client.core.common.marker.YallaMarkerState
import uz.yalla.client.core.common.state.CameraButtonState
import uz.yalla.client.core.common.state.NavigationButtonState
import uz.yalla.client.core.domain.model.Client
import uz.yalla.client.core.domain.model.Destination
import uz.yalla.client.core.domain.model.Executor
import uz.yalla.client.core.domain.model.Location
import uz.yalla.client.core.domain.model.MapPoint
import uz.yalla.client.feature.order.domain.model.response.order.ShowOrderModel

data class HomeState(
    val client: Client?,
    val balance: Long = 0,
    val notificationCount: Int?,

    val isMapEnabled: Boolean,
    val overlayPadding: Dp,
    val sheetHeight: Dp,
    val topPadding: Dp,

    val serviceAvailable: Boolean?,
    val location: Location?,
    val destinations: List<Destination>,
    val route: List<MapPoint>,
    val tariffId: Int?,

    val markerState: YallaMarkerState,
    val carArrivalInMinutes: Int?,
    val orderEndsInMinutes: Int?,

    val orderId: Int?,
    val order: ShowOrderModel?,
    val orders: List<ShowOrderModel>,
    val drivers: List<Executor>,

    val navigationButtonState: NavigationButtonState,
    val cameraButtonState: CameraButtonState,

    val locationEnabled: Boolean,
    val locationGranted: Boolean,

    val ordersSheetVisible: Boolean,
    val permissionDialogVisible: Boolean,
) {
    companion object {
        val INITIAL = HomeState(
            client = null,
            balance = 0,
            notificationCount = null,
            isMapEnabled = true,
            overlayPadding = Dp.Unspecified,
            sheetHeight = Dp.Unspecified,
            topPadding = Dp.Unspecified,
            serviceAvailable = null,
            location = null,
            destinations = emptyList(),
            route = emptyList(),
            tariffId = null,
            markerState = YallaMarkerState.LOADING,
            carArrivalInMinutes = null,
            orderEndsInMinutes = null,
            orderId = null,
            order = null,
            orders = emptyList(),
            drivers = emptyList(),
            ordersSheetVisible = false,
            navigationButtonState = NavigationButtonState.OpenDrawer,
            cameraButtonState = CameraButtonState.MyLocationView,
            locationEnabled = false,
            locationGranted = false,
            permissionDialogVisible = false
        )
    }
}