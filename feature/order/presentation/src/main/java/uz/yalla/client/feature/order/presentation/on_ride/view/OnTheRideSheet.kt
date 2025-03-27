package uz.yalla.client.feature.order.presentation.on_ride.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import org.koin.java.KoinJavaComponent.getKoin
import uz.yalla.client.core.presentation.design.theme.YallaTheme
import uz.yalla.client.feature.order.domain.model.response.order.ShowOrderModel
import uz.yalla.client.feature.order.presentation.R
import uz.yalla.client.feature.order.presentation.on_ride.model.OnTheRideSheetViewModel

object OnTheRideSheet {
    private val viewModel: OnTheRideSheetViewModel by lazy { getKoin().get() }
    internal val mutableIntentFlow = MutableSharedFlow<OnTheRideSheetIntent>()
    val intentFlow = mutableIntentFlow.asSharedFlow()

    @Composable
    fun View(
        car: ShowOrderModel.Executor,
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
                .onSizeChanged {
                    with(density) {
                        viewModel.onIntent(OnTheRideSheetIntent.SetSheetHeight(it.height.toDp()))
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
                    .navigationBarsPadding()
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

                    Text(
                        text = car.driver.stateNumber,
                        style = YallaTheme.font.labelSemiBold,
                        color = YallaTheme.color.black
                    )
                }

                Text(
                    text = "${car.driver.color.name} ${car.driver.mark} ${car.driver.model}",
                    style = YallaTheme.font.label,
                    color = YallaTheme.color.gray
                )
            }

//             Row(
//                horizontalArrangement = Arrangement.spacedBy(10.dp),
//                modifier = Modifier
//                    .height(IntrinsicSize.Min)
//                    .background(
//                        color = YallaTheme.color.white,
//                        shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)
//                    )
//                    .padding(20.dp)
//            ) {
//
//                OptionsButton(
//                    modifier = Modifier.fillMaxHeight(),
//                    painter = painterResource(R.drawable.ic_return),
//                    onClick = {}
//                )
//
//                PrimaryButton (
//                    text = stringResource(R.string.lets_go),
//                    contentPadding = PaddingValues(vertical = 20.dp),
//                    onClick = { },
//                    modifier = Modifier
//                        .weight(1f)
//                        .fillMaxHeight()
//                )
//
//                OptionsButton(
//                    modifier = Modifier.fillMaxHeight(),
//                    painter = painterResource(R.drawable.img_options),
//                    tint = YallaTheme.color.black,
//                    onClick = { }
//                )
//            }
        }
    }
}