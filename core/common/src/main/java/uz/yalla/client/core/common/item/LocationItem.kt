package uz.yalla.client.core.common.item

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import uz.yalla.client.core.presentation.design.theme.YallaTheme

@Composable
fun LocationItem(
    location: String,
    isFirst: Boolean,
    isLast: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = modifier
            .background(
                color = YallaTheme.color.surface,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(16.dp)
    ) {
        when {
            isFirst -> Box(
                Modifier
                    .size(8.dp)
                    .border(
                        color = YallaTheme.color.gray,
                        shape = CircleShape,
                        width = 1.dp
                    )
            )

            isLast -> Box(
                Modifier
                    .size(8.dp)
                    .background(
                        shape = CircleShape,
                        color = YallaTheme.color.primary
                    )
            )

            else -> Box(
                Modifier
                    .size(8.dp)
                    .background(
                        shape = CircleShape,
                        color = YallaTheme.color.gray
                    )
            )
        }

        Text(
            text = location,
            color = YallaTheme.color.onBackground,
            style = YallaTheme.font.labelSemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}