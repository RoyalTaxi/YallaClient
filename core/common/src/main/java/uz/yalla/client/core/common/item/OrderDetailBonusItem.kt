package uz.yalla.client.core.common.item

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
import uz.yalla.client.core.presentation.design.theme.YallaTheme

@Composable
fun OrderDetailBonusItem(
    title: String,
    bonus: String? = null,
    bodyText: String? = null,
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
                    style = YallaTheme.font.body
                )
            }
        }

        bonus?.let {
            Text(
                text = it,
                color = YallaTheme.color.primary,
                style = YallaTheme.font.labelSemiBold
            )
        }
    }
}

fun Int.formatWithSpaces(): String {
    return toString().reversed().chunked(3).joinToString(" ").reversed()
}