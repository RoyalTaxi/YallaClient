package uz.yalla.client.feature.map.presentation.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.navigation.NavHostController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import uz.yalla.client.core.common.map.MapStrategy
import uz.yalla.client.core.common.marker.YallaMarkerState
import uz.yalla.client.core.common.state.HamburgerButtonState
import uz.yalla.client.core.common.state.MoveCameraButtonState
import uz.yalla.client.core.domain.model.OrderStatus
import uz.yalla.client.feature.map.presentation.model.MapUIState
import uz.yalla.client.feature.map.presentation.navigation.BottomSheetNavHost
import uz.yalla.client.feature.map.presentation.view.sheets.ActiveOrdersBottomSheet
import uz.yalla.client.feature.order.presentation.main.MAIN_SHEET_ROUTE
import uz.yalla.client.feature.order.presentation.order_canceled.ORDER_CANCELED_ROUTE

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    map: MapStrategy,
    state: MapUIState,
    hasLocationPermission: Boolean,
    isLocationEnabled: Boolean,
    moveCameraButtonState: MoveCameraButtonState,
    hamburgerButtonState: HamburgerButtonState,
    navController: NavHostController,
    onIntent: (MapScreenIntent) -> Unit,
) {
    val density = LocalDensity.current
    val activeOrdersSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val currentRoute = navController.currentBackStackEntry?.destination?.route ?: MAIN_SHEET_ROUTE
    val isMapEnabled by remember(Unit, state.selectedOrder, state.markerState) {
        mutableStateOf(
            when {
                state.markerState == YallaMarkerState.Searching -> false
                state.selectedOrder == null -> true
                OrderStatus.nonInteractive.contains(state.selectedOrder.status) -> false
                else -> true
            }
        )
    }

    LaunchedEffect(state.isActiveOrdersSheetVisibility) {
        launch(Dispatchers.Main.immediate) {
            if (state.isActiveOrdersSheetVisibility) activeOrdersSheetState.show()
            else activeOrdersSheetState.hide()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        map.Map(
            startingPoint = state.selectedLocation?.point,
            modifier = Modifier.fillMaxSize(),
            enabled = isMapEnabled,
            onMapReady = {
                onIntent(MapScreenIntent.MapOverlayIntent.OnMapReady)
            },
            contentPadding = with(density) {
                PaddingValues(
                    top = WindowInsets.statusBars.getTop(density).toDp(),
                    bottom = state.sheetHeight
                )
            }
        )

        if (isMapEnabled.not()) Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {}
        )

        if (currentRoute.contains(ORDER_CANCELED_ROUTE).not()) {
            MapOverlay(
                modifier = Modifier.padding(bottom = state.overlayPadding),
                state = state,
                hasLocationPermission = hasLocationPermission,
                isLocationEnabled = isLocationEnabled,
                moveCameraButtonState = moveCameraButtonState,
                hamburgerButtonState = hamburgerButtonState,
                onIntent = onIntent
            )
        }

        BottomSheetNavHost(
            navController = navController,
            modifier = Modifier.fillMaxSize()
        )
    }

    if (state.isActiveOrdersSheetVisibility) {
        ActiveOrdersBottomSheet(
            sheetState = activeOrdersSheetState,
            orders = state.orders,
            onSelectOrder = {
                onIntent(MapScreenIntent.SetShowingOrder(it))
            },
            onDismissRequest = {
                onIntent(MapScreenIntent.OnDismissActiveOrders)
            }
        )
    }
}
