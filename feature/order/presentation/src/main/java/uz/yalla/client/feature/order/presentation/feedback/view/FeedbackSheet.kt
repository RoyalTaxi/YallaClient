package uz.yalla.client.feature.order.presentation.feedback.view

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import uz.yalla.client.core.common.button.PrimaryButton
import uz.yalla.client.core.presentation.design.theme.YallaTheme
import uz.yalla.client.feature.order.presentation.R
import uz.yalla.client.feature.order.presentation.components.RatingStarsItem
import uz.yalla.client.feature.order.presentation.coordinator.SheetCoordinator
import uz.yalla.client.feature.order.presentation.feedback.FEEDBACK_ROUTE
import uz.yalla.client.feature.order.presentation.feedback.model.FeedbackSheetViewModel

object FeedbackSheet {
    private val viewModel: FeedbackSheetViewModel by lazy { getKoin().get() }
    internal val mutableIntentFlow = MutableSharedFlow<FeedbackSheetIntent>()
    val intentFlow = mutableIntentFlow.asSharedFlow()

    @Composable
    fun View(
        orderID: Int,
    ) {
        val density = LocalDensity.current
        val state by viewModel.uiState.collectAsState()
        var rating by remember { mutableIntStateOf(0) }

        LaunchedEffect(Unit) {
            launch(Dispatchers.IO) {
                viewModel.setOrderId(orderID)
            }
        }

        BackHandler { viewModel.onIntent(FeedbackSheetIntent.OnCompleteOrder) }

        Box(
            modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter
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
                                route = FEEDBACK_ROUTE,
                                height = it.height.toDp()
                            )
                        }
                    }) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier
                        .fillMaxWidth()
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
                            text = stringResource(R.string.order_completed),
                            style = YallaTheme.font.title,
                            color = YallaTheme.color.black
                        )

                        state.order?.let { order ->
                            Text(
                                text = order.executor.driver.stateNumber,
                                style = YallaTheme.font.labelSemiBold,
                                color = YallaTheme.color.black
                            )
                        }
                    }

                    state.order?.let { order ->
                        Text(
                            text = "${order.executor.driver.color.name} ${order.executor.driver.mark} ${order.executor.driver.model}",
                            style = YallaTheme.font.label,
                            color = YallaTheme.color.gray
                        )
                    }
                }

                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = YallaTheme.color.white, shape = RoundedCornerShape(30.dp)
                        )
                        .padding(20.dp)
                ) {
                    state.order?.let { order ->
                        Text(
                            text = stringResource(R.string.fixed_cost, order.taxi.totalPrice),
                            style = YallaTheme.font.title,
                            color = YallaTheme.color.black
                        )

                        Text(
                            text = if (order.paymentType == "card") stringResource(R.string.with_card)
                            else stringResource(R.string.cash),
                            style = YallaTheme.font.label,
                            color = YallaTheme.color.gray
                        )
                    }
                }

                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = YallaTheme.color.white, shape = RoundedCornerShape(30.dp)
                        )
                        .padding(20.dp)
                ) {
                    Text(
                        text = stringResource(R.string.rate_the_trip),
                        style = YallaTheme.font.title,
                        color = YallaTheme.color.black
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    RatingStarsItem(
                        maxRating = 5,
                        currentRating = rating,
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        onRatingChange = { rating = it })
                }

                Box(
                    modifier = Modifier
                        .background(
                            color = YallaTheme.color.white,
                            shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)
                        )
                        .navigationBarsPadding()
                ) {
                    PrimaryButton(
                        text = if (rating == 0) stringResource(R.string.create_new_order)
                        else stringResource(R.string.rate),
                        onClick = {
                            viewModel.onIntent(FeedbackSheetIntent.OnCompleteOrder)
                            if (rating != 0) viewModel.rateTheRide(rating) },
                        modifier = Modifier
                            .padding(20.dp)
                            .fillMaxWidth()
                    )
                }
            }
        }
    }
}