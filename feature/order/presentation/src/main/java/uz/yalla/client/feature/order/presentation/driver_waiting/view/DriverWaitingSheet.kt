package uz.yalla.client.feature.order.presentation.driver_waiting.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.getKoin
import uz.yalla.client.core.common.button.CallButton
import uz.yalla.client.core.presentation.design.theme.YallaTheme
import uz.yalla.client.feature.order.domain.model.response.order.ShowOrderModel
import uz.yalla.client.feature.order.presentation.R
import uz.yalla.client.feature.order.presentation.components.SearchCarItem
import uz.yalla.client.feature.order.presentation.driver_waiting.model.DriverWaitingViewModel
import kotlin.time.Duration.Companion.seconds

object DriverWaitingSheet {
    private val viewModel: DriverWaitingViewModel by lazy { getKoin().get() }
    internal val mutableIntentFlow = MutableSharedFlow<DriverWaitingIntent>()
    val intentFlow = mutableIntentFlow.asSharedFlow()

    @Composable
    fun View(
        car: ShowOrderModel.Executor,
    ) {
        val density = LocalDensity.current
        var time by rememberSaveable { mutableIntStateOf(0) }

        LaunchedEffect(Unit) {
            launch(Dispatchers.IO) {
                viewModel.setSelectedDriver(car)
            }

            launch(Dispatchers.Default) {
                delay(1.seconds)
                time += 1
            }
        }

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
                        viewModel.onIntent(DriverWaitingIntent.SetSheetHeight(it.height.toDp()))
                    }
                }) {
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier
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

                    Text(
                        text = car.driver.stateNumber,
                        style = YallaTheme.font.labelSemiBold,
                        color = YallaTheme.color.black
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()

                ) {
                    Text(
                        text = "${car.driver.color.name} ${car.driver.mark} ${car.driver.model}",
                        style = YallaTheme.font.label,
                        color = YallaTheme.color.gray
                    )

                    Text(
                        text = time.toString(),
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
                    onClick = { }
                )

                SearchCarItem(
                    text = stringResource(R.string.cancel_order),
                    imageVector = Icons.Default.Close,
                    onClick = { }
                )
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
//            OptionsButton(
//                modifier = Modifier.fillMaxHeight(),
//                tint = YallaTheme.color.red,
//                painter = painterResource(R.drawable.ic_x),
//                onClick = onCancel
//            )

                CallButton(
                    onClick = {  },
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                )

//            OptionsButton(
//                modifier = Modifier.fillMaxHeight(),
//                painter = painterResource(R.drawable.img_options),
//                tint = YallaTheme.color.black,
//                onClick = onOptionsClick
//            )
            }
        }
    }
}