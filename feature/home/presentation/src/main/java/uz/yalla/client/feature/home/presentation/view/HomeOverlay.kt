package uz.yalla.client.feature.home.presentation.view

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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import uz.yalla.client.core.analytics.event.Logger
import uz.yalla.client.core.common.button.BonusOverlay
import uz.yalla.client.core.common.button.EnableGPSButton
import uz.yalla.client.core.common.button.MapButton
import uz.yalla.client.core.common.marker.YallaMarker
import uz.yalla.client.core.common.marker.YallaMarkerState
import uz.yalla.client.core.common.state.CameraButtonState
import uz.yalla.client.core.common.state.NavigationButtonState
import uz.yalla.client.core.domain.model.OrderStatus
import uz.yalla.client.core.presentation.design.theme.YallaTheme
import uz.yalla.client.feature.home.presentation.R
import uz.yalla.client.feature.home.presentation.components.button.ShowActiveOrdersButton
import uz.yalla.client.feature.home.presentation.intent.HomeIntent.HomeOverlayIntent
import uz.yalla.client.feature.home.presentation.intent.HomeState
import kotlin.math.abs

@Composable
fun BoxScope.HomeOverlay(
    modifier: Modifier,
    state: HomeState,
    onIntent: (HomeOverlayIntent) -> Unit
) {
    val context = LocalContext.current
    Box(
        modifier = modifier
            .matchParentSize()
            .padding(20.dp)
    ) {
        if (
            OrderStatus.nonInteractive.contains(state.order?.status) ||
            state.destinations.isEmpty() && state.order == null
        ) YallaMarker(
            state = state.markerState,
            color = YallaTheme.color.primary,
            modifier = Modifier
                .align(Alignment.Center)
                .statusBarsPadding()
                .padding(bottom = abs(state.sheetHeight.value - state.overlayPadding.value).dp)
        )

        if (state.markerState !is YallaMarkerState.Searching) {
            if (state.route.isNotEmpty() || (state.locationGranted && state.locationEnabled)) {
                MapButton(
                    painter = painterResource(
                        if (state.cameraButtonState == CameraButtonState.MyRouteView) R.drawable.ic_route
                        else R.drawable.ic_location
                    ),
                    modifier = Modifier.align(Alignment.BottomEnd),
                    onClick = {
                        when (state.cameraButtonState) {
                            CameraButtonState.MyLocationView -> onIntent(
                                HomeOverlayIntent.AnimateToMyLocation(context)
                            )

                            CameraButtonState.MyRouteView -> onIntent(HomeOverlayIntent.AnimateToMyRoute)
                            CameraButtonState.FirstLocation -> onIntent(HomeOverlayIntent.AnimateToFirstLocation)
                        }
                    }
                )
            } else EnableGPSButton(
                modifier = Modifier.align(Alignment.BottomEnd),
            ) {
                onIntent(
                    if (state.locationEnabled.not()) HomeOverlayIntent.AskForEnable
                    else HomeOverlayIntent.AskForPermission
                )
            }
        }

        Row(
            modifier = Modifier
                .statusBarsPadding()
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            MapButton(
                painter = painterResource(
                    if (state.navigationButtonState == NavigationButtonState.OpenDrawer) R.drawable.ic_hamburger
                    else R.drawable.ic_arrow_back
                ),
                onClick = {
                    if (state.navigationButtonState == NavigationButtonState.OpenDrawer) onIntent(
                        HomeOverlayIntent.OpenDrawer
                    )
                    else onIntent(HomeOverlayIntent.NavigateBack)
                }
            )

            if (state.order == null) {
                state.client?.balance?.let {
                    BonusOverlay(
                        amount = it,
                        onClick = {
                            onIntent(HomeOverlayIntent.OnClickBonus)
                        }
                    )
                }
            }
        }

        if (state.orders.size > 1 || (state.orders.isNotEmpty() && state.order == null))
            ShowActiveOrdersButton(
                orderCount = state.orders.size,
                onClick = { onIntent(HomeOverlayIntent.ClickShowOrders) },
                modifier = Modifier.align(Alignment.BottomCenter)
            )
    }
}