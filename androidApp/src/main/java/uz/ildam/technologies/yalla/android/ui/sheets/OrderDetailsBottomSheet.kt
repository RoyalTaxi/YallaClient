package uz.ildam.technologies.yalla.android.ui.sheets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import uz.ildam.technologies.yalla.android.R
import uz.ildam.technologies.yalla.android.design.theme.YallaTheme
import uz.ildam.technologies.yalla.android.ui.components.item.CarNumberItem
import uz.ildam.technologies.yalla.android.ui.components.item.LocationItem
import uz.ildam.technologies.yalla.core.domain.utils.Formation.toFormattedPrice
import uz.ildam.technologies.yalla.feature.history.domain.model.OrderHistoryModel

@Composable
fun OrderDetailsBottomSheet(
    order: OrderHistoryModel
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier
            .fillMaxHeight(.9f)
            .background(YallaTheme.color.gray2)
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

            LocationItem(
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
                    shape = RoundedCornerShape(30.dp)
                )
                .padding(10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(
                    text = stringResource(R.string.date_and_time),
                    color = YallaTheme.color.black,
                    style = YallaTheme.font.labelSemiBold
                )

                Text(
                    text = "${order.date}, ${order.time}",
                    color = YallaTheme.color.black,
                    style = YallaTheme.font.label
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(
                    text = stringResource(R.string.status),
                    color = YallaTheme.color.black,
                    style = YallaTheme.font.labelSemiBold
                )

                Text(
                    text = order.status,
                    color = YallaTheme.color.primary,
                    style = YallaTheme.font.labelSemiBold
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Column {
                    Text(
                        text = stringResource(R.string.payment),
                        color = YallaTheme.color.black,
                        style = YallaTheme.font.labelSemiBold
                    )

                    Text(
                        text = stringResource(R.string.cash),
                        color = YallaTheme.color.gray,
                        style = YallaTheme.font.bodySmall
                    )
                }

                Text(
                    text = stringResource(
                        R.string.fixed_cost,
                        order.taxi.totalPrice.toString().toFormattedPrice()
                    ),
                    color = YallaTheme.color.black,
                    style = YallaTheme.font.label
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Column {
                    Text(
                        text = stringResource(R.string.car),
                        color = YallaTheme.color.black,
                        style = YallaTheme.font.labelSemiBold
                    )

                    Text(
                        text = "${order.executor.driver.mark} ${order.executor.driver.model}",
                        color = YallaTheme.color.gray,
                        style = YallaTheme.font.bodySmall
                    )
                }

                if (order.executor.driver.stateNumber.length == 8) CarNumberItem(
                    code = order.executor.driver.stateNumber.slice(0..<2),
                    number = "(\\d+|[A-Za-z]+)"
                        .toRegex()
                        .findAll(order.executor.driver.stateNumber.slice(2..7))
                        .map { it.value }
                        .toList()
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(
                    text = stringResource(R.string.tariff),
                    color = YallaTheme.color.black,
                    style = YallaTheme.font.labelSemiBold
                )

                Text(
                    text = order.taxi.tariff,
                    color = YallaTheme.color.black,
                    style = YallaTheme.font.label
                )
            }
        }
    }
}