package uz.yalla.client.feature.map.presentation.components.item

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import uz.yalla.client.core.common.R
import uz.yalla.client.core.common.item.CarNumberItem
import uz.yalla.client.core.common.utils.getOrderStatusText
import uz.yalla.client.core.presentation.design.theme.YallaTheme
import uz.yalla.client.feature.order.domain.model.response.order.ShowOrderModel

@Composable
fun ActiveOrderItem(
    order: ShowOrderModel,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    ) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(YallaTheme.color.gray2),
        shape = RoundedCornerShape(16.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(10.dp)
                .height(IntrinsicSize.Min)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = getOrderStatusText(order.status),
                    color = YallaTheme.color.black,
                    style = YallaTheme.font.labelSemiBold
                )

                order.executor.driver.let {
                    Text(
                        text = "${it.color.name} ${it.mark} ${it.model}",
                        style = YallaTheme.font.bodySmall,
                        color = YallaTheme.color.gray
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .border(
                                shape = CircleShape,
                                width = 1.dp,
                                color = YallaTheme.color.primary
                            )
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Text(
                        text = order.taxi.routes.firstOrNull()?.fullAddress.orEmpty(),
                        color = YallaTheme.color.black,
                        style = YallaTheme.font.labelSemiBold
                    )
                }

                order.taxi.routes.lastOrNull()
                    ?.takeIf { order.taxi.routes.size > 1 }?.fullAddress?.let {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .border(
                                    shape = CircleShape,
                                    width = 1.dp,
                                    color = YallaTheme.color.red
                                )
                        )

                        Spacer(modifier = Modifier.width(10.dp))

                        Text(
                            text = it,
                            color = YallaTheme.color.gray,
                            style = YallaTheme.font.bodySmall
                        )
                    }
                }
            }

            Column(
                horizontalAlignment = Alignment.End,
                modifier = Modifier.heightIn(min = 80.dp)
            ) {
                val stateNumber = order.executor.driver.stateNumber
                if (stateNumber.isNotBlank() && stateNumber.length > 7) {
                    CarNumberItem(
                        code = stateNumber.slice(0..<2),
                        number = "(\\d+|[A-Za-z]+)"
                            .toRegex()
                            .findAll(stateNumber.slice(2..7))
                            .map { it.value }
                            .toList()
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                Image(
                    painter = painterResource(R.drawable.img_default_car),
                    contentDescription = null,
                    modifier = Modifier.height(30.dp)
                )
            }
        }
    }
}