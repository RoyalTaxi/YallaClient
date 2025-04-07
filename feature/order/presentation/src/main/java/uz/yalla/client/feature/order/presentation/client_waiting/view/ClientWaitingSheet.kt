package uz.yalla.client.feature.order.presentation.client_waiting.view

import android.content.Intent
import android.content.Intent.ACTION_DIAL
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
import androidx.compose.runtime.collectAsState
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.getKoin
import uz.yalla.client.core.common.button.CallButton
import uz.yalla.client.core.common.sheet.ConfirmationBottomSheet
import uz.yalla.client.core.presentation.design.theme.YallaTheme
import uz.yalla.client.feature.order.presentation.R
import uz.yalla.client.feature.order.presentation.client_waiting.CLIENT_WAITING_ROUTE
import uz.yalla.client.feature.order.presentation.client_waiting.model.ClientWaitingViewModel
import uz.yalla.client.feature.order.presentation.components.DriverInfoItem
import uz.yalla.client.feature.order.presentation.components.OrderActionsItem
import uz.yalla.client.feature.order.presentation.components.OrderSheetHeader
import uz.yalla.client.feature.order.presentation.coordinator.SheetCoordinator
import uz.yalla.client.feature.order.presentation.main.view.sheet.OrderDetailsBottomSheet

object ClientWaitingSheet {
    private val viewModel: ClientWaitingViewModel by lazy { getKoin().get() }
    internal val mutableIntentFlow = MutableSharedFlow<ClientWaitingIntent>()
    val intentFlow = mutableIntentFlow.asSharedFlow()

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun View(
        orderId: Int
    ) {
        val context = LocalContext.current
        val density = LocalDensity.current
        val state by viewModel.uiState.collectAsState()
        val scope = rememberCoroutineScope()
        val cancelOrderSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        val orderDetailsSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)


        LaunchedEffect(Unit) {
            launch(Dispatchers.IO) {
                viewModel.setOrderId(orderId)
            }
        }

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomCenter
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .pointerInput(Unit) {}
                    .background(
                        color = YallaTheme.color.gray2,
                        shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)
                    )
                    .onSizeChanged {
                        with(density) {
                            SheetCoordinator.updateSheetHeight(
                                route = CLIENT_WAITING_ROUTE,
                                height = it.height.toDp()
                            )
                        }
                    }
            ) {

                OrderSheetHeader(
                    text = stringResource(R.string.coming_to_you),
                    selectedDriver = state.selectedOrder
                )

                Column(
                    modifier = Modifier
                        .clip(RoundedCornerShape(30.dp))
                        .background(YallaTheme.color.white)
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
                        onClick = { viewModel.onIntent(ClientWaitingIntent.AddNewOrder) }
                    )

                    OrderActionsItem(
                        text = stringResource(R.string.order_details),
                        imageVector = Icons.Outlined.Info,
                        onClick = { viewModel.setDetailsBottomSheetVisibility(true) }
                    )
                }

                Box(
                    modifier = Modifier
                        .background(
                            color = YallaTheme.color.white,
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
                            if (intent.resolveActivity(context.packageManager) != null) {
                                context.startActivity(intent)
                            }
                        }
                    )
                }
            }
        }

        if (state.cancelBottomSheetVisibility) {
            ConfirmationBottomSheet(
                sheetState = cancelOrderSheetState,
                title = stringResource(R.string.cancel_order_question),
                description = stringResource(R.string.cancel_order_definition),
                actionText = stringResource(R.string.cancel),
                dismissText = stringResource(R.string.wait),
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
                    onDismissRequest = {
                        viewModel.setDetailsBottomSheetVisibility(false)
                    }
                )
            }
        }

        DisposableEffect(Unit) {
            onDispose {
                viewModel.onIntent(ClientWaitingIntent.UpdateRoute(emptyList()))
                viewModel.clearState()
            }
        }
    }
}