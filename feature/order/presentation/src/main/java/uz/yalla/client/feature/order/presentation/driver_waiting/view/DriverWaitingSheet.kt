package uz.yalla.client.feature.order.presentation.driver_waiting.view

import android.content.Intent
import android.content.Intent.ACTION_DIAL
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.getKoin
import uz.yalla.client.core.common.button.CallButton
import uz.yalla.client.core.common.sheet.ConfirmationBottomSheet
import uz.yalla.client.core.presentation.design.theme.YallaTheme
import uz.yalla.client.feature.order.presentation.R
import uz.yalla.client.feature.order.presentation.components.SearchCarItem
import uz.yalla.client.feature.order.presentation.coordinator.SheetCoordinator
import uz.yalla.client.feature.order.presentation.driver_waiting.DRIVER_WAITING_ROUTE
import uz.yalla.client.feature.order.presentation.driver_waiting.model.DriverWaitingViewModel
import uz.yalla.client.feature.order.presentation.main.view.sheet.OrderDetailsBottomSheet
import java.util.Locale

object DriverWaitingSheet {
    private val viewModel: DriverWaitingViewModel by lazy { getKoin().get() }
    internal val mutableIntentFlow = MutableSharedFlow<DriverWaitingIntent>()
    val intentFlow = mutableIntentFlow.asSharedFlow()

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun View(
        orderId: Int,
    ) {
        val context = LocalContext.current
        val density = LocalDensity.current
        val scope = rememberCoroutineScope()
        val state by viewModel.uiState.collectAsState()
        var timer by remember { mutableStateOf("") }
        val cancelOrderSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        val orderDetailsSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)


        LaunchedEffect(Unit) {
            launch(Dispatchers.IO) {
                viewModel.setOrderId(orderId)
            }

            launch(Dispatchers.Default) {
                viewModel.infiniteTimer(true).collectLatest { seconds ->
                    val minutes = seconds / 60
                    val sec = seconds % 60
                    timer = String.format(Locale.US, "%02d:%02d", minutes, sec)
                }
            }
        }

        BackHandler { viewModel.setCancelBottomSheetVisibility(true) }

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomCenter
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = YallaTheme.color.gray2,
                        shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)
                    )
                    .onSizeChanged {
                        with(density) {
                            SheetCoordinator.updateSheetHeight(
                                route = DRIVER_WAITING_ROUTE,
                                height = it.height.toDp()
                            )
                        }
                    }
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier
                        .background(
                            color = YallaTheme.color.white, shape = RoundedCornerShape(30.dp)
                        )
                        .padding(20.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = stringResource(R.string.waiting_for_you),
                            style = YallaTheme.font.title,
                            color = YallaTheme.color.black
                        )

                        state.selectedDriver?.let { order ->
                            Text(
                                text = order.executor.driver.stateNumber,
                                style = YallaTheme.font.labelSemiBold,
                                color = YallaTheme.color.black
                            )
                        }
                    }
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()

                    ) {
                        state.selectedDriver?.let { order ->
                            Text(
                                text = "${order.executor.driver.color.name} ${order.executor.driver.mark} ${order.executor.driver.model}",
                                style = YallaTheme.font.label,
                                color = YallaTheme.color.gray
                            )
                        }

                        Text(
                            text = timer,
                            style = YallaTheme.font.label,
                            color = YallaTheme.color.primary
                        )
                    }
                }

                Column(
                    modifier = Modifier
                        .clip(RoundedCornerShape(30.dp))
                        .background(YallaTheme.color.white)
                ) {
                    SearchCarItem(
                        text = stringResource(R.string.order_details),
                        imageVector = Icons.Outlined.Info,
                        onClick = { viewModel.setDetailsBottomSheetVisibility(true) })

                    SearchCarItem(
                        text = stringResource(R.string.add_order),
                        imageVector = Icons.Default.Add,
                        onClick = { viewModel.onIntent(DriverWaitingIntent.AddNewOrder) }
                    )

                    SearchCarItem(
                        text = stringResource(R.string.cancel_order),
                        imageVector = Icons.Default.Close,
                        onClick = { viewModel.setCancelBottomSheetVisibility(true) })
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier
                        .height(IntrinsicSize.Min)
                        .background(
                            color = YallaTheme.color.white,
                            shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)
                        )
                        .navigationBarsPadding()
                        .padding(20.dp)
                ) {

                    CallButton(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        onClick = {
                            val phoneNumber =
                                state.selectedDriver?.executor?.phone ?: return@CallButton
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