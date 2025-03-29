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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import uz.yalla.client.feature.order.presentation.coordinator.SheetCoordinator
import uz.yalla.client.feature.order.presentation.on_the_ride.ON_THE_RIDE_ROUTE
import uz.yalla.client.feature.order.presentation.on_the_ride.model.OnTheRideSheetViewModel

object OnTheRideSheet {
    private val viewModel: OnTheRideSheetViewModel by lazy { getKoin().get() }
    internal val mutableIntentFlow = MutableSharedFlow<OnTheRideSheetIntent>()
    val intentFlow = mutableIntentFlow.asSharedFlow()

    @Composable
    fun View(
        orderId: Int
    ) {
        val density = LocalDensity.current
        val state by viewModel.uiState.collectAsState()

        LaunchedEffect(Unit) {
            launch(Dispatchers.IO) {
                viewModel.setOrderId(orderId)
            }
        }

        BackHandler { }

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

                        state.selectedDriver?.let { executor ->
                            Text(
                                text = executor.driver.stateNumber,
                                style = YallaTheme.font.labelSemiBold,
                                color = YallaTheme.color.black
                            )
                        }
                    }

                    state.selectedDriver?.let { executor ->
                        Text(
                            text = "${executor.driver.color.name} ${executor.driver.mark} ${executor.driver.model}",
                            style = YallaTheme.font.label,
                            color = YallaTheme.color.gray
                        )
                    }
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
}