package uz.yalla.client.feature.map.presentation.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import uz.yalla.client.core.common.button.BonusOverlay
import uz.yalla.client.core.common.button.EnableGPSButton
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
    hasLocationPermission: Boolean,
    isLocationEnabled: Boolean,
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
            state.isMarkerVisible &&
            (OrderStatus.nonInteractive.contains(state.selectedOrder?.status) ||
                    (state.destinations.isEmpty() && state.selectedOrder == null))
        ) YallaMarker(
            state = state.markerState,
            color = YallaTheme.color.primary,
            modifier = Modifier
                .align(Alignment.Center)
                .statusBarsPadding()
        )

        if (state.markerState !is YallaMarkerState.Searching) {
            if (state.route.isNotEmpty() || (hasLocationPermission && isLocationEnabled)) {
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
            } else EnableGPSButton(
                modifier = Modifier.align(Alignment.BottomEnd),
            ) {
                onIntent(
                    if (isLocationEnabled.not()) MapOverlayIntent.AskForEnable
                    else MapOverlayIntent.AskForPermission
                )
            }
        }

        Row(
            modifier = Modifier.statusBarsPadding().fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            MapButton(
                painter = painterResource(
                    if (hamburgerButtonState == HamburgerButtonState.OpenDrawer) R.drawable.ic_hamburger
                    else R.drawable.ic_arrow_back
                ),
                modifier = Modifier,
                onClick = {
                    if (hamburgerButtonState == HamburgerButtonState.OpenDrawer) onIntent(
                        MapOverlayIntent.OpenDrawer
                    )
                    else onIntent(MapOverlayIntent.NavigateBack)
                }
            )

            if (state.selectedOrder == null) {
                state.user?.client?.balance?.let {
                    BonusOverlay(
                        amount = it,
                        onClick = { onIntent(MapOverlayIntent.OnClickBonus) }
                    )
                }
            }
        }

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