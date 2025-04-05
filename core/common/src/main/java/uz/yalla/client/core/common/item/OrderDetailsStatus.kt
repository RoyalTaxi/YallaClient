package uz.yalla.client.core.common.item

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import uz.yalla.client.core.common.utils.getOrderStatusText
import uz.yalla.client.core.domain.model.OrderStatus
import uz.yalla.client.core.presentation.design.theme.YallaTheme

@Composable
fun OrderDetailsStatus(
    title: String,
    status: OrderStatus
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 20.dp,
                vertical = 10.dp
            )
    ) {
        Text(
            text = title,
            color = YallaTheme.color.black,
            style = YallaTheme.font.labelSemiBold
        )

        val statusColor = when (status) {
            OrderStatus.Completed -> YallaTheme.color.primary
            OrderStatus.Canceled -> YallaTheme.color.red
            else -> YallaTheme.color.red
        }

        Text(
            text = getOrderStatusText(status),
            color = statusColor,
            style = YallaTheme.font.labelSemiBold
        )
    }
}