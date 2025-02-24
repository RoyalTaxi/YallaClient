package uz.ildam.technologies.yalla.android.ui.screens.map

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.morfly.compose.bottomsheet.material3.BottomSheetScaffold
import io.morfly.compose.bottomsheet.material3.BottomSheetScaffoldState
import kotlinx.coroutines.launch
import uz.ildam.technologies.yalla.android.ui.sheets.BottomSheetFooter
import uz.ildam.technologies.yalla.core.domain.model.MapPoint
import uz.ildam.technologies.yalla.feature.order.domain.model.response.order.OrderStatus
import uz.yalla.client.feature.core.design.theme.YallaTheme
import uz.yalla.client.feature.core.map.MapStrategy
import uz.yalla.client.feature.core.sheets.SheetValue

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun MapScreen(
    uiState: MapUIState,
    map: MapStrategy,
    isLoading: Boolean,
    currentLatLng: MutableState<MapPoint>,
    scaffoldState: BottomSheetScaffoldState<SheetValue>,
    activeOrdersState: SheetState,
    mapSheetHandler: MapSheetHandler,
    mapBottomSheetHandler: MapBottomSheetHandler,
    onIntent: (MapIntent) -> Unit,
    onCreateOrder: () -> Unit,
    onAppear: (Dp) -> Unit,
    onClearOptions: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val density = LocalDensity.current
    val disabledStatuses = remember {
        mutableStateListOf(
            OrderStatus.New,
            OrderStatus.NonStopSending,
            OrderStatus.UserSending,
            OrderStatus.Sending
        )
    }

    Box {
        BottomSheetScaffold(
            modifier = Modifier.fillMaxSize(),
            scaffoldState = scaffoldState,
            sheetDragHandle = null,
            sheetShape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp),
            sheetContainerColor = YallaTheme.color.gray2,
            sheetContent = {
                mapSheetHandler.Sheets(
                    isLoading = isLoading,
                    uiState = uiState,
                    currentLatLng = currentLatLng
                )
            }
        ) {
            map.Map(
                modifier = Modifier,
                contentPadding = with(density) {
                    PaddingValues(
                        top = WindowInsets.statusBars.getTop(density).toDp(),
                        bottom = uiState.primarySheetHeight + uiState.footerHeight -
                                if (uiState.outOfService == true) uiState.outOfServicePadding else 0.dp
                    )
                }
            )

            if (disabledStatuses.contains(uiState.selectedDriver?.status)) Box(
                modifier = Modifier
                    .matchParentSize()
                    .pointerInput(Unit) { detectTapGestures { } }
            )

            if (scaffoldState.sheetState.targetValue != SheetValue.Expanded) MapOverlay(
                modifier = Modifier.padding(
                    bottom = uiState.primarySheetHeight + uiState.footerHeight -
                            if (uiState.outOfService == true) uiState.outOfServicePadding else 0.dp
                ),
                uiState = uiState,
                isLoading = isLoading,
                onIntent = onIntent,
                activeOrdersState = activeOrdersState,
                onClickShowOrders = { visible ->
                    mapBottomSheetHandler.showActiveOrders(
                        visible.not()
                    )
                }
            )
        }

        if (uiState.selectedDriver == null && uiState.outOfService != true) BottomSheetFooter(
            uiState = uiState,
            isLoading = isLoading,
            sheetState = scaffoldState.sheetState,
            onCreateOrder = onCreateOrder,
            onSelectPaymentMethodClick = {
                mapBottomSheetHandler.showPaymentMethod(show = true)
            },
            showOptions = { expand ->
                scope.launch {
                    scaffoldState.sheetState.animateTo(
                        if (expand) SheetValue.Expanded else SheetValue.PartiallyExpanded
                    )
                }
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .onSizeChanged { onAppear(with(density) { it.height.toDp() }) },
            cleanOptions = onClearOptions
        )
    }
}