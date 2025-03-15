package uz.yalla.client.feature.order.presentation.client_waiting.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.koin.java.KoinJavaComponent.getKoin
import uz.yalla.client.core.common.button.CallButton
import uz.yalla.client.core.common.item.CarNumberItem
import uz.yalla.client.core.presentation.design.theme.YallaTheme
import uz.yalla.client.feature.order.domain.model.response.order.ShowOrderModel
import uz.yalla.client.feature.order.presentation.R
import uz.yalla.client.feature.order.presentation.client_waiting.model.ClientWaitingViewModel

object ClientWaitingSheet {
    val viewModel: ClientWaitingViewModel = getKoin().get()

    @Composable
    fun View(
        car: ShowOrderModel.Executor,
        onClickCall: (String) -> Unit,
        onAppear: (Dp) -> Unit
    ) {
        val density = LocalDensity.current
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = YallaTheme.color.gray2,
                    shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)
                )
                .onSizeChanged { size ->
                    with(density) { onAppear(size.height.toDp()) }
                }
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = YallaTheme.color.white,
                        shape = RoundedCornerShape(30.dp)
                    )
                    .padding(20.dp)
            ) {
                Text(
                    text = stringResource(R.string.coming_to_you),
                    style = YallaTheme.font.title,
                    color = YallaTheme.color.black
                )

                Text(
                    text = "${car.driver.color.name} ${car.driver.mark} ${car.driver.model}",
                    style = YallaTheme.font.label,
                    color = YallaTheme.color.gray
                )

                CarNumberItem(
                    code = car.driver.stateNumber.slice(0..<2),
                    number = "(\\d+|[A-Za-z]+)"
                        .toRegex()
                        .findAll(car.driver.stateNumber)
                        .map { it.value }
                        .toList()
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
                    onClick = { onClickCall(car.phone) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}