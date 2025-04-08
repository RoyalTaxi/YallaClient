package uz.yalla.client.feature.order.presentation.on_the_ride.view

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.morfly.compose.bottomsheet.material3.BottomSheetScaffold
import io.morfly.compose.bottomsheet.material3.rememberBottomSheetScaffoldState
import io.morfly.compose.bottomsheet.material3.rememberBottomSheetState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.getKoin
import uz.yalla.client.core.common.state.SheetValue
import uz.yalla.client.core.presentation.design.theme.YallaTheme
import uz.yalla.client.feature.order.presentation.R
import uz.yalla.client.feature.order.presentation.components.DriverInfoItem
import uz.yalla.client.feature.order.presentation.components.OrderActionsItem
import uz.yalla.client.feature.order.presentation.components.OrderSheetHeader
import uz.yalla.client.feature.order.presentation.coordinator.SheetCoordinator
import uz.yalla.client.feature.order.presentation.driver_waiting.DRIVER_WAITING_ROUTE
import uz.yalla.client.feature.order.presentation.driver_waiting.view.DriverWaitingIntent
import uz.yalla.client.feature.order.presentation.driver_waiting.view.DriverWaitingSheet
import uz.yalla.client.feature.order.presentation.main.view.sheet.OrderDetailsBottomSheet
import uz.yalla.client.feature.order.presentation.on_the_ride.ON_THE_RIDE_ROUTE
import uz.yalla.client.feature.order.presentation.on_the_ride.model.OnTheRideSheetViewModel

object OnTheRideSheet {
    private val viewModel: OnTheRideSheetViewModel by lazy { getKoin().get() }
    internal val mutableIntentFlow = MutableSharedFlow<OnTheRideSheetIntent>()
    val intentFlow = mutableIntentFlow.asSharedFlow()

    @OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
    @Composable
    fun View(
        orderId: Int
    ) {
        val density = LocalDensity.current
        val state by viewModel.uiState.collectAsState()
        val scaffoldState = rememberBottomSheetScaffoldState(
            rememberBottomSheetState(
                initialValue = SheetValue.PartiallyExpanded,
                defineValues = {
                    SheetValue.PartiallyExpanded at height(state.headerHeight + 40.dp)
                    SheetValue.Expanded at contentHeight
                }
            )
        )
        val orderDetailsSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

        LaunchedEffect(Unit) {
            launch(Dispatchers.IO) {
                viewModel.setOrderId(orderId)
            }
        }

        LaunchedEffect(state.headerHeight) {
            launch(Dispatchers.Main) {
                scaffoldState.sheetState.refreshValues()
                SheetCoordinator.updateSheetHeight(
                    route = DRIVER_WAITING_ROUTE,
                    height = state.headerHeight + 40.dp
                )
            }
        }

        BackHandler { }

        BottomSheetScaffold(
            scaffoldState = scaffoldState,
            sheetDragHandle = null,
            sheetContainerColor = YallaTheme.color.gray2,
            content = {},
            sheetContent = {
                Box(
                    contentAlignment = Alignment.BottomCenter
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.background(
                            color = YallaTheme.color.gray2,
                            shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)
                        )
                    ) {

                        OrderSheetHeader(
                            text = stringResource(R.string.on_the_way),
                            selectedDriver = state.selectedDriver,
                            modifier = Modifier
                                .onSizeChanged {
                                    with(density) {
                                        viewModel.onIntent(
                                            OnTheRideSheetIntent.SetHeaderHeight(it.height.toDp())
                                        )
                                    }
                                }
                        )

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp))
                                .background(YallaTheme.color.white)
                                .navigationBarsPadding()
                        ) {

                            state.selectedDriver?.let {
                                DriverInfoItem(
                                    driver = it.executor
                                )
                            }

                            OrderActionsItem(
                                text = stringResource(R.string.add_order),
                                imageVector = Icons.Default.Add,
                                onClick = { viewModel.onIntent(OnTheRideSheetIntent.AddNewOrder) }
                            )

                            OrderActionsItem(
                                text = stringResource(R.string.order_details),
                                imageVector = Icons.Outlined.Info,
                                onClick = { viewModel.setDetailsBottomSheetVisibility(true) }
                            )
                        }
                    }
                }
            }
        )

        if (state.detailsBottomSheetVisibility) {
            state.selectedDriver?.let {
                OrderDetailsBottomSheet(
                    order = it,
                    sheetState = orderDetailsSheetState,
                    onDismissRequest = {
                        viewModel.setDetailsBottomSheetVisibility(false)
                    }
                )
            }
        }
    }
}