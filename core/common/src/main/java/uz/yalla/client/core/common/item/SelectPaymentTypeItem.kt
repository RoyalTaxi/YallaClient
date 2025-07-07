package uz.yalla.client.core.common.item

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import uz.yalla.client.core.presentation.design.theme.YallaTheme

@Composable
fun SelectPaymentTypeItem(
    isSelected: Boolean,
    painter: Painter,
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    tint: Color = Color.Unspecified,
    onSelect: () -> Unit,
) {
    Card(
        modifier = modifier,
        onClick = onSelect,
        enabled = enabled,
        colors = CardDefaults.cardColors(
            containerColor = YallaTheme.color.background,
            disabledContainerColor = YallaTheme.color.background,
            disabledContentColor = YallaTheme.color.gray
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 20.dp,
                    vertical = 12.dp
                )
        ) {
            Icon(
                painter = painter,
                modifier = Modifier
                    .size(28.dp)
                    .alpha(if (enabled) 1f else .5f),
                contentDescription = null,
                tint = tint
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = text,
                color = YallaTheme.color.onBackground.copy(alpha = if (enabled) 1f else .5f),
                style = YallaTheme.font.labelSemiBold
            )

            Spacer(modifier = Modifier.weight(1f))

            Icon(
                imageVector = Icons.Filled.Done,
                contentDescription = null,
                tint = YallaTheme.color.background,
                modifier = Modifier
                    .background(
                        color = if (isSelected) YallaTheme.color.primary else YallaTheme.color.background,
                        shape = CircleShape
                    )
                    .padding(6.dp)
            )
        }
    }
}