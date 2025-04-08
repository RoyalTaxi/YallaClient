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
import uz.yalla.client.core.common.item.OrderDetailItem
import uz.yalla.client.core.common.item.OrderDetailsStatus
import uz.yalla.client.core.common.utils.getOrderStatusText
import uz.yalla.client.core.presentation.design.theme.YallaTheme
import uz.yalla.client.feature.domain.model.OrderHistoryModel
import uz.yalla.client.feature.history.R

@Composable
internal fun OrderDetailsBottomSheet(
    order: OrderHistoryModel,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.background(YallaTheme.color.gray2)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .background(
                    color = YallaTheme.color.white,
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
                    color = YallaTheme.color.white,
                    shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)
                )
                .padding(vertical = 10.dp)
        ) {

            OrderDetailItem(
                title = stringResource(R.string.date_and_time),
                descriptor = "${order.date}, ${order.time}"
            )

            OrderDetailsStatus(
                title = stringResource(R.string.status),
                status = order.status
            )
            OrderDetailItem(
                title = stringResource(R.string.status),
                descriptor = getOrderStatusText(order.status),
            )

            OrderDetailItem(
                title = stringResource(R.string.payment),
                bodyText = stringResource(R.string.cash),
                descriptor = stringResource(
                    R.string.fixed_cost,
                    order.taxi.totalPrice.toString()
                ),
            )

            if (order.executor.driver.stateNumber.isNotEmpty())
                OrderDetailItem(
                    title = stringResource(R.string.car),
                    bodyText = "${order.executor.driver.mark} ${order.executor.driver.model}",
                    carNumber = order.executor.driver.stateNumber
                )

            OrderDetailItem(
                title = stringResource(R.string.tariff),
                descriptor = order.taxi.tariff,
            )
        }
    }
}