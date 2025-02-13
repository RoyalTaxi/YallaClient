package uz.ildam.technologies.yalla.android.ui.sheets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import uz.ildam.technologies.yalla.android.R
import uz.ildam.technologies.yalla.android.ui.components.button.YallaButton
import uz.ildam.technologies.yalla.android.ui.components.item.RatingStarsItem
import uz.ildam.technologies.yalla.feature.order.domain.model.response.order.ShowOrderModel
import uz.yalla.client.feature.core.design.theme.YallaTheme

@Composable
fun FeedbackBottomSheet(
    userRating: Int = 0,
    orderModel: ShowOrderModel,
    onRatingChange: (Int) -> Unit,
    onRate: () -> Unit,
    onDismissRequest: () -> Unit,
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
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier
                .fillMaxWidth()
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
                    text = stringResource(R.string.order_completed),
                    style = YallaTheme.font.title,
                    color = YallaTheme.color.black
                )

                Text(
                    text = orderModel.executor.driver.stateNumber,
                    style = YallaTheme.font.labelSemiBold,
                    color = YallaTheme.color.black
                )
            }

            Text(
                text = "${orderModel.executor.driver.color.name} ${orderModel.executor.driver.mark} ${orderModel.executor.driver.model}",
                style = YallaTheme.font.label,
                color = YallaTheme.color.gray
            )
        }

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
                text = stringResource(R.string.fixed_cost, orderModel.taxi.totalPrice),
                style = YallaTheme.font.title,
                color = YallaTheme.color.black
            )

            Text(
                text = if (orderModel.paymentType == "card")
                    stringResource(R.string.with_card)
                else stringResource(R.string.cash),
                style = YallaTheme.font.label,
                color = YallaTheme.color.gray
            )
        }

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
                currentRating = userRating,
                modifier = Modifier.align(Alignment.CenterHorizontally),
                onRatingChange = { onRatingChange(it) }
            )
        }

        Box(
            modifier = Modifier
                .background(
                    color = YallaTheme.color.white,
                    shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)
                )
                .navigationBarsPadding()
        ) {
            YallaButton(
                text = if (userRating == 0) stringResource(R.string.create_new_order)
                else stringResource(R.string.rate),
                onClick = if (userRating == 0) onDismissRequest else onRate,
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth()
            )
        }
    }
}
