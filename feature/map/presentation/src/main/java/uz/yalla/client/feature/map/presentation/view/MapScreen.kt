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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.navigation.NavHostController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import uz.yalla.client.core.common.map.MapStrategy
import uz.yalla.client.core.common.state.HamburgerButtonState
import uz.yalla.client.core.common.state.MoveCameraButtonState
import uz.yalla.client.feature.map.presentation.model.MapUIState
import uz.yalla.client.feature.map.presentation.navigation.BottomSheetNavHost
import uz.yalla.client.feature.map.presentation.view.sheets.ActiveOrdersBottomSheet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    map: MapStrategy,
    isMapEnabled: Boolean,
    state: MapUIState,
    moveCameraButtonState: MoveCameraButtonState,
    hamburgerButtonState: HamburgerButtonState,
    navController: NavHostController,
    onIntent: (MapScreenIntent) -> Unit
) {
    val density = LocalDensity.current
    val scope = rememberCoroutineScope()
    val activeOrdersSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    Box(modifier = Modifier.fillMaxSize()) {
        map.Map(
            startingPoint = state.selectedLocation?.point,
            modifier = Modifier.fillMaxSize(),
            enabled = isMapEnabled,
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

        MapOverlay(
            modifier = Modifier.padding(bottom = state.sheetHeight),
            state = state,
            moveCameraButtonState = moveCameraButtonState,
            hamburgerButtonState = hamburgerButtonState,
            onIntent = onIntent
        )

        BottomSheetNavHost(
            navController = navController,
            modifier = Modifier.fillMaxSize()
        )
    }

    if (state.isActiveOrdersSheetVisibility) {
        ActiveOrdersBottomSheet(
            sheetState = activeOrdersSheetState,
            orders = state.orders,
            onSelectOrder = { onIntent(MapScreenIntent.SetShowingOrder(it)) },
            onDismissRequest = {
                onIntent(MapScreenIntent.OnDismissActiveOrders)
                scope.launch(Dispatchers.Main) {
                    activeOrdersSheetState.hide()
                }
            }
        )
    }
}