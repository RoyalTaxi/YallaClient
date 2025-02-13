package uz.ildam.technologies.yalla.android.ui.screens.map

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import uz.ildam.technologies.yalla.android.R
import uz.ildam.technologies.yalla.android.ui.components.button.MapButton
import uz.ildam.technologies.yalla.android.ui.components.button.ShowActiveOrdersButton
import uz.ildam.technologies.yalla.android.ui.components.marker.YallaMarker
import uz.ildam.technologies.yalla.feature.order.domain.model.response.order.OrderStatus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BoxScope.MapOverlay(
    modifier: Modifier,
    uiState: MapUIState,
    activeOrdersState: SheetState,
    isLoading: Boolean,
    onClickShowOrders: (visible: Boolean) -> Unit,
    onIntent: (MapIntent) -> Unit
) {
    val disabledStatuses = listOf(
        OrderStatus.New,
        OrderStatus.Sending,
        OrderStatus.UserSending,
        OrderStatus.NonStopSending
    )
    Box(
        modifier = modifier
            .matchParentSize()
            .padding(20.dp)
    ) {
        YallaMarker(
            time = uiState.timeout,
            isLoading = isLoading,
            isSearching = uiState.selectedDriver?.status == OrderStatus.New,
            isRouteEmpty = uiState.route.isEmpty(),
            isSending = uiState.selectedDriver?.status in listOf(
                OrderStatus.Sending,
                OrderStatus.UserSending,
                OrderStatus.NonStopSending
            ),
            isAppointed = uiState.selectedDriver?.status == OrderStatus.Appointed,
            isAtAddress = uiState.selectedDriver?.status == OrderStatus.AtAddress,
            isInFetters = uiState.selectedDriver?.status == OrderStatus.InFetters,
            isCompleted = uiState.selectedDriver?.status == OrderStatus.Completed,
            selectedAddressName = uiState.selectedLocation?.name,
            modifier = Modifier
                .align(Alignment.Center)
                .statusBarsPadding()
        )

        if (uiState.selectedDriver?.status !in disabledStatuses) {
            MapButton(
                painter = painterResource(
                    if (uiState.moveCameraButtonState == MoveCameraButtonState.MyRouteView)
                        R.drawable.ic_route
                    else
                        R.drawable.ic_location
                ),
                modifier = Modifier.align(Alignment.BottomEnd),
                onClick = {
                    when (uiState.moveCameraButtonState) {
                        MoveCameraButtonState.MyLocationView -> onIntent(MapIntent.MoveToMyLocation)
                        MoveCameraButtonState.MyRouteView -> onIntent(MapIntent.MoveToMyRoute)
                        MoveCameraButtonState.FirstLocation -> onIntent(MapIntent.MoveToFirstLocation)
                    }
                }
            )

            MapButton(
                painter = painterResource(
                    if (uiState.discardOrderButtonState == DiscardOrderButtonState.OpenDrawer)
                        R.drawable.ic_hamburger
                    else
                        R.drawable.ic_arrow_back
                ),
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .statusBarsPadding(),
                onClick = {
                    if (uiState.discardOrderButtonState == DiscardOrderButtonState.OpenDrawer)
                        onIntent(MapIntent.OpenDrawer)
                    else
                        onIntent(MapIntent.DiscardOrder)
                }
            )
        }

        if (
            uiState.orders.size > 1 ||
            (uiState.orders.isNotEmpty() && uiState.showingOrderId == null)
        ) ShowActiveOrdersButton(
            orderCount = uiState.orders.size,
            isOpen = activeOrdersState.isVisible,
            onClick = { onClickShowOrders(activeOrdersState.isVisible) },
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}