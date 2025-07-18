package uz.yalla.client.feature.order.presentation.order_canceled.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LocalLifecycleOwner
import org.koin.androidx.compose.koinViewModel
import uz.yalla.client.core.common.button.PrimaryButton
import uz.yalla.client.core.presentation.design.theme.YallaTheme
import uz.yalla.client.feature.order.presentation.R
import uz.yalla.client.feature.order.presentation.coordinator.SheetCoordinator
import uz.yalla.client.feature.order.presentation.order_canceled.ORDER_CANCELED_ROUTE
import uz.yalla.client.feature.order.presentation.order_canceled.model.OrderCanceledSheetViewModel

@Composable
fun OrderCanceledSheet(
    viewModel: OrderCanceledSheetViewModel = koinViewModel()
) {
    val density = LocalDensity.current
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(Unit) {
        OrderCanceledSheetChannel.register(lifecycleOwner)
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .onSizeChanged {
                    with(density) {
                        SheetCoordinator.updateSheetHeight(
                            route = ORDER_CANCELED_ROUTE,
                            height = it.height.toDp()
                        )
                    }
                }
                .background(
                    color = YallaTheme.color.surface,
                    shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = YallaTheme.color.background,
                        shape = RoundedCornerShape(30.dp)
                    )
                    .padding(20.dp)
            ) {
                Text(
                    text = stringResource(R.string.canceled),
                    color = YallaTheme.color.onBackground,
                    style = YallaTheme.font.title
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = stringResource(R.string.order_canceled),
                    color = YallaTheme.color.gray,
                    style = YallaTheme.font.label
                )
            }

            Box(
                modifier = Modifier.background(
                    color = YallaTheme.color.background,
                    shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)
                )
                    .navigationBarsPadding()

            ) {
                PrimaryButton(
                    text = stringResource(R.string.new_order),
                    onClick = { viewModel.onIntent(OrderCanceledSheetIntent.StartNewOrder) },
                    modifier = Modifier
                        .padding(20.dp)
                        .fillMaxWidth()
                )
            }
        }
    }
}