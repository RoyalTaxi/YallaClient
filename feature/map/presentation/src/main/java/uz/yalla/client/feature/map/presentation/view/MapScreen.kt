package uz.yalla.client.feature.map.presentation.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import uz.yalla.client.core.common.map.MapStrategy
import uz.yalla.client.core.common.marker.YallaMarkerState
import uz.yalla.client.core.common.state.HamburgerButtonState
import uz.yalla.client.core.common.state.MoveCameraButtonState
import uz.yalla.client.core.domain.model.OrderStatus
import uz.yalla.client.core.presentation.design.theme.YallaTheme
import uz.yalla.client.feature.map.presentation.R
import uz.yalla.client.feature.map.presentation.components.card.NoInternetCard
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
    networkState: Boolean,
    hasLocationPermission: Boolean,
    isLocationEnabled: Boolean,
    moveCameraButtonState: MoveCameraButtonState,
    hamburgerButtonState: HamburgerButtonState,
    navController: NavHostController,
    onIntent: (MapScreenIntent) -> Unit,
) {
    val density = LocalDensity.current
    val activeOrdersSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val noConnectionTitle = stringResource(id = R.string.no_connection)
    val noConnectionMessage = stringResource(id = R.string.check_your_connection)
    val currentRoute = navController.currentBackStackEntry?.destination?.route ?: MAIN_SHEET_ROUTE
    val snackbarHostState = remember { SnackbarHostState() }
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

    LaunchedEffect(networkState) {
        if (networkState) {
            snackbarHostState.currentSnackbarData?.dismiss()
        } else {
            snackbarHostState.showSnackbar(
                message = noConnectionTitle,
                actionLabel = noConnectionMessage,
                duration = SnackbarDuration.Indefinite,
                withDismissAction = true
            )
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        content = { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {
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

                SnackbarHost(
                    hostState = snackbarHostState,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(WindowInsets.statusBars.asPaddingValues())
                        .consumeWindowInsets(WindowInsets.statusBars.asPaddingValues())
                        .fillMaxWidth(.9f)
                        .clip(RoundedCornerShape(16.dp)),
                    snackbar = { snackbarData: SnackbarData ->
                        Snackbar(
                            containerColor = YallaTheme.color.red,
                            contentColor = YallaTheme.color.background,
                            content = { NoInternetCard(Modifier.fillMaxWidth()) }
                        )
                    }
                )
            }
        }
    )


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
