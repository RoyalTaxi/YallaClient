package uz.yalla.client.feature.order.presentation.on_the_ride.view

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.getKoin
import uz.yalla.client.core.presentation.design.theme.YallaTheme
import uz.yalla.client.feature.order.presentation.R
import uz.yalla.client.feature.order.presentation.components.SearchCarItem
import uz.yalla.client.feature.order.presentation.coordinator.SheetCoordinator
import uz.yalla.client.feature.order.presentation.main.view.sheet.OrderDetailsBottomSheet
import uz.yalla.client.feature.order.presentation.on_the_ride.ON_THE_RIDE_ROUTE
import uz.yalla.client.feature.order.presentation.on_the_ride.model.OnTheRideSheetViewModel

object OnTheRideSheet {
    private val viewModel: OnTheRideSheetViewModel by lazy { getKoin().get() }
    internal val mutableIntentFlow = MutableSharedFlow<OnTheRideSheetIntent>()
    val intentFlow = mutableIntentFlow.asSharedFlow()

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun View(
        orderId: Int
    ) {
        val density = LocalDensity.current
        val state by viewModel.uiState.collectAsState()
        val orderDetailsSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

        LaunchedEffect(Unit) {
            launch(Dispatchers.IO) {
                viewModel.setOrderId(orderId)
            }
        }

        BackHandler { }

        Box(
            modifier = Modifier
                .fillMaxSize(),
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
                                route = ON_THE_RIDE_ROUTE,
                                height = it.height.toDp()
                            )
                        }
                    }
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier
                        .background(
                            color = YallaTheme.color.white,
                            shape = RoundedCornerShape(30.dp)
                        )
                        .padding(20.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = stringResource(R.string.on_the_way),
                            style = YallaTheme.font.title,
                            color = YallaTheme.color.black
                        )

                        state.selectedDriver?.let { driver ->
                            Text(
                                text = driver.executor.driver.stateNumber,
                                style = YallaTheme.font.labelSemiBold,
                                color = YallaTheme.color.black
                            )
                        }
                    }

                    state.selectedDriver?.let { driver ->
                        Text(
                            text = "${driver.executor.driver.color.name} ${driver.executor.driver.mark} ${driver.executor.driver.model}",
                            style = YallaTheme.font.label,
                            color = YallaTheme.color.gray
                        )
                    }
                }

                Column(
                    modifier = Modifier
                        .clip(RoundedCornerShape(30.dp))
                        .background(YallaTheme.color.white)
                        .navigationBarsPadding()
                ) {
                    SearchCarItem(
                        text = stringResource(R.string.order_details),
                        imageVector = Icons.Outlined.Info,
                        onClick = { viewModel.setDetailsBottomSheetVisibility(true) })

                    SearchCarItem(
                        text = stringResource(R.string.add_order),
                        imageVector = Icons.Default.Add,
                        onClick = { viewModel.onIntent(OnTheRideSheetIntent.AddNewOrder) }
                    )
                }
            }
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