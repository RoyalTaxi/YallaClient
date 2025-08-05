package uz.yalla.client.feature.payment.card_list.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
fun CardListItem(
    isSelected: Boolean,
    painter: Painter,
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    editCardEnabled: Boolean = false,
    tint: Color = Color.Unspecified,
    onSelect: () -> Unit,
    onDelete: () -> Unit
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

            if (editCardEnabled) {
                IconButton(
                    onClick = onDelete,
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = null,
                        tint = YallaTheme.color.onBackground,
                        modifier = Modifier.padding(6.dp)
                    )
                }
            } else {

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
}