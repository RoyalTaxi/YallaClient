package uz.ildam.technologies.yalla.android.ui.screens.map

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import io.morfly.compose.bottomsheet.material3.BottomSheetScaffold
import io.morfly.compose.bottomsheet.material3.BottomSheetScaffoldState
import io.morfly.compose.bottomsheet.material3.requireSheetVisibleHeightDp
import uz.ildam.technologies.yalla.android.ui.sheets.SheetValue
import uz.ildam.technologies.yalla.core.domain.model.MapPoint
import uz.ildam.technologies.yalla.feature.order.domain.model.response.order.OrderStatus
import kotlin.math.abs

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
) {
    val disabledStatuses = remember {
        mutableStateListOf(
            OrderStatus.New,
            OrderStatus.NonStopSending,
            OrderStatus.UserSending,
            OrderStatus.Sending
        )
    }

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetDragHandle = null,
        sheetShape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp),
        sheetContent = {
            mapSheetHandler.Sheets(
                isLoading = isLoading,
                uiState = uiState,
                currentLatLng = currentLatLng,
                onCreateOrder = onCreateOrder
            )
        },
        content = {
            val bottomPadding by remember {
                derivedStateOf { scaffoldState.sheetState.requireSheetVisibleHeightDp() }
            }
            val adjustedBottomPadding = remember(bottomPadding) { abs(bottomPadding.value - 24).dp }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = adjustedBottomPadding)
            ) {
                map.Map(modifier = Modifier)

                if (disabledStatuses.contains(uiState.selectedDriver?.status)) Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(Unit) { detectTapGestures { } }
                )

                MapOverlay(
                    uiState = uiState,
                    isLoading = isLoading,
                    onIntent = onIntent,
                    activeOrdersState = activeOrdersState,
                    onClickShowOrders = { visible -> mapBottomSheetHandler.showActiveOrders(visible.not()) }
                )
            }
        }
    )
}