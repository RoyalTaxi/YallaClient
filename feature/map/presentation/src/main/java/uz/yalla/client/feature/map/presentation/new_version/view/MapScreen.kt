package uz.yalla.client.feature.map.presentation.new_version.view

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
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import uz.yalla.client.core.presentation.design.theme.YallaTheme
import uz.yalla.client.feature.map.presentation.components.card.NoInternetCard
import uz.yalla.client.feature.map.presentation.new_version.intent.MapDrawerIntent
import uz.yalla.client.feature.map.presentation.new_version.intent.MapIntent
import uz.yalla.client.feature.map.presentation.new_version.intent.MapIntent.MapOverlayIntent.OpenDrawer
import uz.yalla.client.feature.map.presentation.new_version.intent.MapState
import uz.yalla.client.feature.map.presentation.new_version.navigation.BottomSheetNavHost
import uz.yalla.client.feature.map.presentation.new_version.sheets.ActiveOrdersBottomSheet
import uz.yalla.client.feature.order.presentation.main.MAIN_SHEET_ROUTE
import uz.yalla.client.feature.order.presentation.order_canceled.ORDER_CANCELED_ROUTE

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MScreen(
    state: MapState,
    networkState: Boolean,
    navController: NavHostController,
    onIntent: (MapIntent) -> Unit,
    onDrawerIntent: (MapDrawerIntent) -> Unit = {}
) {
    val context = LocalContext.current
    val snackBarHostState = remember { SnackbarHostState() }
    val currentRoute = navController.currentBackStackEntry?.destination?.route ?: MAIN_SHEET_ROUTE
    val ordersSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var isDrawerOpen by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (networkState) snackBarHostState.currentSnackbarData?.dismiss()
        else snackBarHostState.showSnackbar(message = "")
    }

    NavigationDrawer(
        isOpen = isDrawerOpen,
        onDismiss = { isDrawerOpen = false },
        client = state.client,
        notificationsCount = state.notificationCount ?: 0,
        onIntent = { drawerIntent ->
            isDrawerOpen = false
            onDrawerIntent(drawerIntent)
        }
    )

    Box(modifier = Modifier.fillMaxSize()) {
        if (currentRoute.contains(ORDER_CANCELED_ROUTE).not()) {
            MapOverlay(
                state = state,
                onIntent = { mapIntent ->
                    if (mapIntent is OpenDrawer) isDrawerOpen = true
                    else onIntent(mapIntent)
                },
                modifier = Modifier
                    .padding(bottom = state.overlayPadding)
                    .onSizeChanged {
                        onIntent(MapIntent.MapOverlayIntent.MoveToMyLocation(context))
                    }
            )
        }

        if (state.isMapEnabled.not()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {}
            )
        }

        SnackbarHost(
            hostState = snackBarHostState,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(WindowInsets.statusBars.asPaddingValues())
                .consumeWindowInsets(WindowInsets.statusBars.asPaddingValues())
                .fillMaxWidth(.9f)
                .clip(RoundedCornerShape(16.dp)),
            snackbar = {
                Snackbar(
                    containerColor = YallaTheme.color.red,
                    contentColor = YallaTheme.color.background,
                    content = { NoInternetCard(Modifier.fillMaxWidth()) }
                )
            }
        )
    }

    BottomSheetNavHost(
        navController = navController,
        modifier = Modifier.fillMaxSize()
    )

    if (state.ordersSheetVisible) {
        ActiveOrdersBottomSheet(
            sheetState = ordersSheetState,
            orders = state.orders,
            onSelectOrder = { onIntent(MapIntent.SetShowingOrder(it)) },
            onDismissRequest = { onIntent(MapIntent.OnDismissActiveOrders) }
        )
    }
}