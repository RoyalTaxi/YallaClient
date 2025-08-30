package uz.yalla.client.feature.order.presentation.client_waiting.view

import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.Intent.ACTION_DIAL
import android.widget.Toast
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.morfly.compose.bottomsheet.material3.BottomSheetScaffold
import io.morfly.compose.bottomsheet.material3.rememberBottomSheetScaffoldState
import io.morfly.compose.bottomsheet.material3.rememberBottomSheetState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import uz.yalla.client.core.common.button.CallButton
import uz.yalla.client.core.common.sheet.ConfirmationBottomSheet
import uz.yalla.client.core.common.state.SheetValue
import uz.yalla.client.core.presentation.design.theme.YallaTheme
import uz.yalla.client.feature.order.presentation.R
import uz.yalla.client.feature.order.presentation.client_waiting.CLIENT_WAITING_ROUTE
import uz.yalla.client.feature.order.presentation.client_waiting.model.ClientWaitingViewModel
import uz.yalla.client.feature.order.presentation.components.OrderSheetHeader
import uz.yalla.client.feature.order.presentation.components.items.DriverInfoItem
import uz.yalla.client.feature.order.presentation.components.items.OrderActionsItem
import uz.yalla.client.feature.order.presentation.coordinator.SheetCoordinator
import uz.yalla.client.feature.order.presentation.main.view.sheet.OrderDetailsBottomSheet

 val mutableIntentFlow = MutableSharedFlow<ClientWaitingSheetIntent>()
val intentFlow = mutableIntentFlow.asSharedFlow()

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ClientWaitingSheet(
    orderId: Int,
    viewModel: ClientWaitingViewModel = koinViewModel()
) {
    val density = LocalDensity.current
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val scaffoldState = rememberBottomSheetScaffoldState(
        rememberBottomSheetState(
            initialValue = SheetValue.PartiallyExpanded,
            defineValues = {
                SheetValue.PartiallyExpanded at height(state.headerHeight + state.footerHeight + 40.dp)
                SheetValue.Expanded at contentHeight
            }
        )
    )
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val lifecycleOwner = LocalLifecycleOwner.current
    val cancelOrderSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val orderDetailsSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    BackHandler { viewModel.onIntent(ClientWaitingSheetIntent.AddNewOrder) }

    LaunchedEffect(Unit) {
        ClientWaitingSheetChannel.register(lifecycleOwner)
    }

    LaunchedEffect(Unit) {
        launch(Dispatchers.IO) {
            viewModel.setOrderId(orderId)
        }
    }

    LaunchedEffect(state.footerHeight, state.headerHeight) {
        launch(Dispatchers.Main) {
            scaffoldState.sheetState.refreshValues()
            SheetCoordinator.updateSheetHeight(
                route = CLIENT_WAITING_ROUTE,
                height = state.headerHeight + state.footerHeight + 40.dp
            )
        }
    }

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetDragHandle = null,
        sheetContainerColor = YallaTheme.color.surface,
        content = {},
        sheetContent = {
            Box(
                contentAlignment = Alignment.BottomCenter
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier
                        .background(
                            color = YallaTheme.color.surface,
                            shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)
                        )
                        .padding(bottom = state.footerHeight + 10.dp)
                ) {
                    OrderSheetHeader(
                        text = stringResource(R.string.coming_to_you),
                        selectedDriver = state.selectedOrder,
                        modifier = Modifier
                            .onSizeChanged {
                                with(density) {
                                    viewModel.onIntent(
                                        ClientWaitingSheetIntent.SetHeaderHeight(it.height.toDp())
                                    )
                                }
                            }
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(30.dp))
                            .background(color = YallaTheme.color.background)
                    ) {

                        state.selectedOrder?.let {
                            DriverInfoItem(
                                driver = it.executor
                            )
                        }

                        OrderActionsItem(
                            text = stringResource(R.string.cancel_order),
                            imageVector = Icons.Default.Close,
                            onClick = { viewModel.setCancelBottomSheetVisibility(true) }
                        )

                        OrderActionsItem(
                            text = stringResource(R.string.add_order),
                            imageVector = Icons.Default.Add,
                            onClick = { viewModel.onIntent(ClientWaitingSheetIntent.AddNewOrder) }
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

    Box(
        contentAlignment = Alignment.BottomCenter,
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .pointerInput(Unit) {}
                .onSizeChanged {
                    with(density) {
                        viewModel.onIntent(
                            ClientWaitingSheetIntent.SetFooterHeight(it.height.toDp())
                        )
                    }
                }
                .background(
                    color = YallaTheme.color.background,
                    shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)
                )
                .padding(20.dp)
                .navigationBarsPadding()
        ) {
            CallButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    val phoneNumber =
                        state.selectedOrder?.executor?.phone ?: return@CallButton
                    val intent =
                        Intent(ACTION_DIAL).apply { data = "tel:$phoneNumber".toUri() }
                    try {
                        context.startActivity(intent)
                    } catch (e: ActivityNotFoundException) {
                        Toast.makeText(context, "No dialer app found", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            )
        }
    }


    if (state.cancelBottomSheetVisibility) {
        ConfirmationBottomSheet(
            sheetState = cancelOrderSheetState,
            title = stringResource(R.string.cancel_order_question),
            description = stringResource(R.string.cancel_order_definition),
            actionText = stringResource(R.string.cancel),
            dismissText = stringResource(R.string.wait),
            actionEnabled = state.isOrderCancellable,
            onDismissRequest = {
                viewModel.setCancelBottomSheetVisibility(false)
                scope.launch {
                    cancelOrderSheetState.hide()
                }
            },
            onConfirm = {
                viewModel.cancelRide()
                viewModel.setCancelBottomSheetVisibility(false)
                scope.launch {
                    cancelOrderSheetState.hide()
                }
            }
        )
    }

    if (state.detailsBottomSheetVisibility) {
        state.selectedOrder?.let {
            OrderDetailsBottomSheet(
                order = it,
                sheetState = orderDetailsSheetState,
                onDismissRequest = { viewModel.setDetailsBottomSheetVisibility(false) }
            )
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.onIntent(ClientWaitingSheetIntent.UpdateRoute(emptyList()))
            viewModel.clearState()
        }
    }
}