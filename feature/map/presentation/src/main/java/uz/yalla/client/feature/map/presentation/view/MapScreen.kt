package uz.yalla.client.feature.map.presentation.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import uz.yalla.client.core.common.marker.YallaMarkerState
import uz.yalla.client.core.domain.model.OrderStatus
import uz.yalla.client.core.presentation.design.theme.YallaTheme
import uz.yalla.client.feature.map.presentation.R
import uz.yalla.client.feature.map.presentation.components.card.NoInternetCard
import uz.yalla.client.feature.map.presentation.new_version.navigation.BottomSheetNavHost
import uz.yalla.client.feature.map.presentation.new_version.sheets.ActiveOrdersBottomSheet
import uz.yalla.client.feature.order.presentation.main.MAIN_SHEET_ROUTE
import uz.yalla.client.feature.order.presentation.order_canceled.ORDER_CANCELED_ROUTE

//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun MapScreen(
//    state: MState,
//    networkState: Boolean,
//    navController: NavHostController,
//    onIntent: (MapScreenIntent) -> Unit,
//) {
//    val activeOrdersSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
//    val noConnectionTitle = stringResource(id = R.string.no_connection)
//    val noConnectionMessage = stringResource(id = R.string.check_your_connection)
//    val currentRoute = navController.currentBackStackEntry?.destination?.route ?: MAIN_SHEET_ROUTE
//    val snackbarHostState = remember { SnackbarHostState() }
//    val isMapEnabled by remember(Unit, state.selectedOrder, state.markerState) {
//        mutableStateOf(
//            when {
//                state.markerState == YallaMarkerState.Searching -> false
//                state.order == null -> true
//                OrderStatus.nonInteractive.contains(state.selectedOrder.status) -> false
//                else -> true
//            }
//        )
//    }
//
//    LaunchedEffect(state.isActiveOrdersSheetVisibility) {
//        launch(Dispatchers.Main.immediate) {
//            if (state.isActiveOrdersSheetVisibility) activeOrdersSheetState.show()
//            else activeOrdersSheetState.hide()
//        }
//    }
//
//    LaunchedEffect(networkState) {
//        if (networkState) {
//            snackbarHostState.currentSnackbarData?.dismiss()
//        } else {
//            snackbarHostState.showSnackbar(
//                message = noConnectionTitle,
//                actionLabel = noConnectionMessage,
//                duration = SnackbarDuration.Indefinite,
//                withDismissAction = true
//            )
//        }
//    }
//
//    // Replace Scaffold with Box - this doesn't consume touch events
//    Box(
//        modifier = Modifier.fillMaxSize()
//    ) {
//        // Map layer - should be at the bottom to receive touch events
////        map.Map(
////            startingPoint = state.selectedLocation?.point,
////            modifier = Modifier.fillMaxSize(),
////            enabled = isMapEnabled,
////            onMapReady = {
////                onIntent(MapScreenIntent.MapOverlayIntent.OnMapReady)
////            },
////            contentPadding = with(density) {
////                PaddingValues(
////                    top = WindowInsets.statusBars.getTop(density).toDp(),
////                    bottom = state.sheetHeight
////                )
////            }
////        )
//
//        // Only add blocking overlay when map is disabled
////        if (isMapEnabled.not()) {
////            Box(
////                modifier = Modifier
////                    .fillMaxSize()
////                    .pointerInput(Unit) {
////                        // Consume all touch events when map is disabled
////                    }
////            )
////        }
//
//        // Bottom sheet navigation
//        BottomSheetNavHost(
//            navController = navController,
//            modifier = Modifier.fillMaxSize()
//        )
//
//        // Map overlay with buttons
//        if (currentRoute.contains(ORDER_CANCELED_ROUTE).not()) {
//            MapOverlay(
//                modifier = Modifier.padding(bottom = state.overlayPadding),
//                state = state,
//                hasLocationPermission = hasLocationPermission,
//                isLocationEnabled = isLocationEnabled,
//                cameraButtonState = cameraButtonState,
//                navigationButtonState = navigationButtonState,
//                onIntent = onIntent
//            )
//        }
//
//        SnackbarHost(
//            hostState = snackbarHostState,
//            modifier = Modifier
//                .align(Alignment.TopCenter)
//                .padding(WindowInsets.statusBars.asPaddingValues())
//                .consumeWindowInsets(WindowInsets.statusBars.asPaddingValues())
//                .fillMaxWidth(.9f)
//                .clip(RoundedCornerShape(16.dp)),
//            snackbar = {
//                Snackbar(
//                    containerColor = YallaTheme.color.red,
//                    contentColor = YallaTheme.color.background,
//                    content = { NoInternetCard(Modifier.fillMaxWidth()) }
//                )
//            }
//        )
//    }
//
//    if (state.ordersSheetVisible) {
//        ActiveOrdersBottomSheet(
//            sheetState = activeOrdersSheetState,
//            orders = state.orders,
//            onSelectOrder = {
//                onIntent(MapScreenIntent.SetShowingOrder(it))
//            },
//            onDismissRequest = {
//                onIntent(MapScreenIntent.OnDismissActiveOrders)
//            }
//        )
//    }
//}