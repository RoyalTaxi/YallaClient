package uz.yalla.client.feature.map.presentation.view

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.navigation.NavHostController
import uz.yalla.client.core.common.map.MapStrategy
import uz.yalla.client.core.common.state.HamburgerButtonState
import uz.yalla.client.core.common.state.MoveCameraButtonState
import uz.yalla.client.feature.map.presentation.model.MapUIState
import uz.yalla.client.feature.map.presentation.navigation.BottomSheetNavHost

@Composable
fun MapScreen(
    map: MapStrategy,
    isMapEnabled: Boolean,
    state: MapUIState,
    moveCameraButtonState: MoveCameraButtonState,
    hamburgerButtonState: HamburgerButtonState,
    navController: NavHostController,
    onIntent: (MapOverlayIntent) -> Unit
) {
    val density = LocalDensity.current

    val animatedSheetHeight by animateDpAsState(
        targetValue = state.sheetHeight,
        animationSpec = tween(
            durationMillis = 200,
            easing = FastOutSlowInEasing
        ),
        label = "sheet_height_animation"
    )

    Box(modifier = Modifier.fillMaxSize()) {
        map.Map(
            startingPoint = null,
            modifier = Modifier.fillMaxSize(),
            enabled = isMapEnabled,
            contentPadding = with(density) {
                PaddingValues(
                    top = WindowInsets.statusBars.getTop(density).toDp(),
                    bottom = state.sheetHeight
                )
            }
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
            modifier = Modifier.fillMaxSize(),
        )
    }
}