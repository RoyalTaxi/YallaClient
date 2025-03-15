package uz.yalla.client.feature.order.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import uz.yalla.client.core.common.item.CarNumberItem
import uz.yalla.client.core.presentation.design.theme.YallaTheme

@Composable
fun OrderDetailItem(
    title: String,
    modifier: Modifier = Modifier,
    bodyText: String? = null,
    descriptor: String? = null,
    carNumber: String? = null
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
            .fillMaxWidth()
            .padding(20.dp)
    ) {
        Column {
            Text(
                text = title,
                color = YallaTheme.color.black,
                style = YallaTheme.font.labelSemiBold
            )

            bodyText?.let {
                Text(
                    text = it,
                    color = YallaTheme.color.gray,
                    style = YallaTheme.font.bodySmall
                )
            }
        }

        if (carNumber != null && carNumber.length > 7) CarNumberItem(
            code = carNumber.slice(0..<2),
            number = "(\\d+|[A-Za-z]+)"
                .toRegex()
                .findAll(carNumber.slice(2..7))
                .map { it.value }
                .toList()
        )

        descriptor?.let {
            Text(
                text = it,
                color = YallaTheme.color.black,
                style = YallaTheme.font.label
            )
        }
    }
}