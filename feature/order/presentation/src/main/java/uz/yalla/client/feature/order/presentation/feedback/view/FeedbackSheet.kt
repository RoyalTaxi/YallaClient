package uz.yalla.client.feature.order.presentation.feedback.view

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.morfly.compose.bottomsheet.material3.BottomSheetScaffold
import io.morfly.compose.bottomsheet.material3.rememberBottomSheetScaffoldState
import io.morfly.compose.bottomsheet.material3.rememberBottomSheetState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.getKoin
import uz.yalla.client.core.common.button.PrimaryButton
import uz.yalla.client.core.common.state.SheetValue
import uz.yalla.client.core.domain.model.PaymentType
import uz.yalla.client.core.presentation.design.theme.YallaTheme
import uz.yalla.client.feature.order.domain.model.response.order.ShowOrderModel
import uz.yalla.client.feature.order.presentation.R
import uz.yalla.client.feature.order.presentation.components.OrderSheetHeader
import uz.yalla.client.feature.order.presentation.components.items.RatingStarsItem
import uz.yalla.client.feature.order.presentation.coordinator.SheetCoordinator
import uz.yalla.client.feature.order.presentation.feedback.model.FeedbackSheetViewModel
import uz.yalla.client.feature.order.presentation.on_the_ride.ON_THE_RIDE_ROUTE

object FeedbackSheet {
    private val viewModel: FeedbackSheetViewModel by lazy { getKoin().get() }
    internal val mutableIntentFlow = MutableSharedFlow<FeedbackSheetIntent>()
    val intentFlow = mutableIntentFlow.asSharedFlow()

    @OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
    @Composable
    fun View(
        orderID: Int,
    ) {
        val density = LocalDensity.current
        val state by viewModel.uiState.collectAsState()
        var rating by remember { mutableIntStateOf(0) }
        val scaffoldState = rememberBottomSheetScaffoldState(
            rememberBottomSheetState(
                initialValue = SheetValue.Expanded,
                defineValues = {
                    SheetValue.PartiallyExpanded at height(state.headerHeight + state.footerHeight + 40.dp)
                    SheetValue.Expanded at contentHeight
                }
            )
        )

        LaunchedEffect(Unit) {
            launch(Dispatchers.IO) {
                viewModel.setOrderId(orderID)
            }
        }

        LaunchedEffect(state.footerHeight, state.headerHeight) {
            launch(Dispatchers.Main) {
                scaffoldState.sheetState.refreshValues()
                SheetCoordinator.updateSheetHeight(
                    route = ON_THE_RIDE_ROUTE,
                    height = state.headerHeight + state.footerHeight + 40.dp
                )
            }
        }

        BackHandler { viewModel.onIntent(FeedbackSheetIntent.OnCompleteOrder) }

        BottomSheetScaffold(
            scaffoldState = scaffoldState,
            sheetDragHandle = null,
            sheetContainerColor = YallaTheme.color.gray2,
            content = {},
            sheetContent = {
                Box(
                    contentAlignment = Alignment.BottomCenter
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier
                            .background(
                                color = YallaTheme.color.gray2,
                                shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)
                            )
                            .padding(bottom = state.footerHeight + 10.dp)
                    ) {
                        OrderSheetHeader(
                            text = stringResource(R.string.order_completed),
                            selectedDriver = state.order,
                            modifier = Modifier
                                .onSizeChanged {
                                    with(density) {
                                        viewModel.onIntent(
                                            FeedbackSheetIntent.SetHeaderHeight(it.height.toDp())
                                        )
                                    }
                                }
                        )

                        FeedbackOrderInfo(
                            order = state.order
                        )

                        Column(
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    color = YallaTheme.color.white,
                                    shape = RoundedCornerShape(30.dp)
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
                                onRatingChange = { rating = it }
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
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .pointerInput(Unit) {}
                    .onSizeChanged {
                        with(density) {
                            viewModel.onIntent(
                                FeedbackSheetIntent.SetFooterHeight(it.height.toDp())
                            )
                        }
                    }
                    .background(
                        color = YallaTheme.color.white,
                        shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)
                    )
                    .navigationBarsPadding()
                    .padding(20.dp)
            ) {
                PrimaryButton(
                    text = if (rating == 0) stringResource(R.string.create_new_order)
                    else stringResource(R.string.rate),
                    onClick = {
                        viewModel.onIntent(FeedbackSheetIntent.OnCompleteOrder)
                        if (rating != 0) viewModel.rateTheRide(rating)
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun FeedbackOrderInfo(
    order: ShowOrderModel? = null
) {
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
            text = stringResource(R.string.driver),
            style = YallaTheme.font.title,
            color = YallaTheme.color.black
        )

        Text(
            text = "${order?.executor?.surName} ${order?.executor?.givenNames} ${order?.executor?.fatherName}",
            style = YallaTheme.font.label,
            color = YallaTheme.color.gray
        )

        Spacer(modifier = Modifier.height(10.dp))

        order?.let { order ->
            Text(
                text = stringResource(R.string.fixed_cost, order.taxi.totalPrice),
                style = YallaTheme.font.title,
                color = YallaTheme.color.black
            )

            Text(
                text = when (order.paymentType) {
                    is PaymentType.CARD -> stringResource(R.string.with_card)
                    else -> stringResource(R.string.cash)
                },
                style = YallaTheme.font.label,
                color = YallaTheme.color.gray
            )
        }
    }
}