package uz.yalla.client.feature.map.presentation.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import uz.yalla.client.core.common.button.MapButton
import uz.yalla.client.core.common.marker.YallaMarker
import uz.yalla.client.core.common.marker.YallaMarkerState
import uz.yalla.client.core.common.state.HamburgerButtonState
import uz.yalla.client.core.common.state.MoveCameraButtonState
import uz.yalla.client.core.domain.model.OrderStatus
import uz.yalla.client.core.presentation.design.theme.YallaTheme
import uz.yalla.client.feature.map.presentation.R
import uz.yalla.client.feature.map.presentation.components.button.ShowActiveOrdersButton
import uz.yalla.client.feature.map.presentation.model.MapUIState
import uz.yalla.client.feature.map.presentation.view.MapScreenIntent.MapOverlayIntent

@Composable
fun BoxScope.MapOverlay(
    modifier: Modifier,
    state: MapUIState,
    moveCameraButtonState: MoveCameraButtonState,
    hamburgerButtonState: HamburgerButtonState,
    onIntent: (MapOverlayIntent) -> Unit
) {
    Box(
        modifier = modifier
            .matchParentSize()
            .padding(20.dp)
    ) {
        if (
            OrderStatus.nonInteractive.contains(state.selectedOrder?.status)
            ||
            (state.destinations.isEmpty() && state.selectedOrder == null)
        ) YallaMarker(
            state = state.markerState,
            color = YallaTheme.color.primary,
            modifier = Modifier
                .align(Alignment.Center)
                .statusBarsPadding()
        )

        if (state.markerState !is YallaMarkerState.Searching) {
            MapButton(
                painter = painterResource(
                    if (moveCameraButtonState == MoveCameraButtonState.MyRouteView) R.drawable.ic_route
                    else R.drawable.ic_location
                ),
                modifier = Modifier.align(Alignment.BottomEnd),
                onClick = {
                    when (moveCameraButtonState) {
                        MoveCameraButtonState.MyLocationView -> onIntent(MapOverlayIntent.AnimateToMyLocation)
                        MoveCameraButtonState.MyRouteView -> onIntent(MapOverlayIntent.MoveToMyRoute)
                        MoveCameraButtonState.FirstLocation -> onIntent(MapOverlayIntent.MoveToFirstLocation)
                    }
                }
            )
        }

        MapButton(
            painter = painterResource(
                if (hamburgerButtonState == HamburgerButtonState.OpenDrawer) R.drawable.ic_hamburger
                else R.drawable.ic_arrow_back
            ),
            modifier = Modifier
                .align(Alignment.TopStart)
                .statusBarsPadding(),
            onClick = {
                if (hamburgerButtonState == HamburgerButtonState.OpenDrawer) onIntent(
                    MapOverlayIntent.OpenDrawer
                )
                else onIntent(MapOverlayIntent.NavigateBack)
            }
        )

        if (
            state.orders.size > 1 ||
            (state.orders.isNotEmpty() && state.selectedOrder == null)
        ) ShowActiveOrdersButton(
            orderCount = state.orders.size,
            onClick = { onIntent(MapOverlayIntent.ClickShowOrders) },
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}