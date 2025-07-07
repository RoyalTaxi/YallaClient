package uz.yalla.client.feature.history.history.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import uz.yalla.client.core.common.R
import uz.yalla.client.core.common.utils.getOrderStatusText
import uz.yalla.client.core.domain.model.OrderStatus
import uz.yalla.client.core.presentation.design.theme.YallaTheme
import uz.yalla.client.feature.domain.model.OrdersHistory

@Composable
fun OrderHistoryItem(
    order: OrdersHistory.Item,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(YallaTheme.color.surface),
        shape = RoundedCornerShape(16.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(10.dp)
                .height(IntrinsicSize.Min)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .border(
                                shape = CircleShape,
                                width = 1.dp,
                                color = YallaTheme.color.gray
                            )
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Text(
                        text = order.taxi.routes.firstOrNull()?.fullAddress.orEmpty(),
                        color = YallaTheme.color.onBackground,
                        style = YallaTheme.font.labelSemiBold
                    )
                }

                val secondAddress = order.taxi.routes.lastOrNull()
                    .takeIf { order.taxi.routes.size > 1 }?.fullAddress
                secondAddress?.let {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .background(
                                    color = YallaTheme.color.primary,
                                    shape = CircleShape
                                )
                        )

                        Spacer(modifier = Modifier.width(10.dp))

                        Text(
                            text = it,
                            color = YallaTheme.color.gray,
                            style = YallaTheme.font.body
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = order.time,
                    color = YallaTheme.color.gray,
                    style = YallaTheme.font.body,
                )
            }

            Column(
                horizontalAlignment = Alignment.End,
                modifier = Modifier.heightIn(min = 80.dp)
            ) {
                val statusColor = when (order.status) {
                    OrderStatus.Completed -> YallaTheme.color.primary
                    OrderStatus.Canceled -> YallaTheme.color.red
                    else -> YallaTheme.color.red
                }

                Text(
                    text = stringResource(R.string.fixed_cost, order.taxi.totalPrice),
                    color = YallaTheme.color.onBackground,
                    style = YallaTheme.font.labelSemiBold,
                    textAlign = TextAlign.End
                )

                Text(
                    text = getOrderStatusText(order.status),
                    color = statusColor,
                    style = YallaTheme.font.body
                )

                Spacer(modifier = Modifier.weight(1f))

                Spacer(modifier = Modifier.height(8.dp))

                Image(
                    painter = painterResource(R.drawable.img_default_car),
                    contentDescription = null,
                    modifier = Modifier.height(30.dp)
                )
            }
        }
    }
}