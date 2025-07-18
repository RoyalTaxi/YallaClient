package uz.yalla.client.feature.history.history_details.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import uz.yalla.client.core.common.item.LocationItem
import uz.yalla.client.core.common.item.OrderDetailBonusItem
import uz.yalla.client.core.common.item.OrderDetailItem
import uz.yalla.client.core.common.item.OrderDetailsStatus
import uz.yalla.client.core.common.item.formatWithSpaces
import uz.yalla.client.core.domain.model.GivenAwardPaymentType
import uz.yalla.client.core.presentation.design.theme.YallaTheme
import uz.yalla.client.feature.domain.model.OrderHistoryModel
import uz.yalla.client.feature.history.R

@Composable
internal fun OrderDetailsBottomSheet(
    order: OrderHistoryModel,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.background(YallaTheme.color.surface)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .background(
                    color = YallaTheme.color.background,
                    shape = RoundedCornerShape(bottomStart = 30.dp, bottomEnd = 30.dp)
                )
                .padding(20.dp)
        ) {
            LocationItem(
                location = order.taxi.routes.firstOrNull()?.fullAddress.orEmpty(),
                isFirst = true,
                isLast = false,
                modifier = Modifier.fillMaxWidth()
            )

            for (index in 1 until order.taxi.routes.size - 1) {
                LocationItem(
                    location = order.taxi.routes[index].fullAddress,
                    isFirst = false,
                    isLast = false,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            if (order.taxi.routes.size > 1) LocationItem(
                location = order.taxi.routes.lastOrNull()?.fullAddress.orEmpty(),
                isFirst = false,
                isLast = true,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = YallaTheme.color.background,
                    shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)
                )
                .padding(vertical = 10.dp)
        ) {

            OrderDetailItem(
                title = stringResource(R.string.date_and_time),
                description = "${order.date}, ${order.time}"
            )

            OrderDetailsStatus(
                title = stringResource(R.string.status),
                status = order.status
            )

            OrderDetailItem(
                title = stringResource(R.string.payment),
                bodyText = stringResource(R.string.cash),
                description = stringResource(
                    R.string.fixed_cost,
                    order.taxi.totalPrice.formatWithSpaces()
                ),
            )

            order.taxi.bonusAmount.takeIf { it != 0 }?.let {
                OrderDetailBonusItem(
                    title = stringResource(R.string.with_bonus),
                    bonus = it.formatWithSpaces()
                )
            }

            order.award.paymentAward.takeIf { it != 0 }?.let {
                OrderDetailBonusItem(
                    title = stringResource(R.string.cashback),
                    bonus = order.award.paymentAward.toString(),
                    bodyText = when (order.award.paymentType) {
                        is GivenAwardPaymentType.BALANCE -> stringResource(R.string.to_bonus_balance)
                        is GivenAwardPaymentType.PAYNET -> stringResource(R.string.to_phone_number)
                    }
                )
            }

            if (order.executor.driver.stateNumber.isNotEmpty())
                OrderDetailItem(
                    title = stringResource(R.string.car),
                    bodyText = "${order.executor.driver.mark} ${order.executor.driver.model}",
                    carNumber = order.executor.driver.stateNumber
                )

            OrderDetailItem(
                title = stringResource(R.string.tariff),
                description = order.taxi.tariff,
            )
        }
    }
}