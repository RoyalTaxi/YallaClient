package uz.yalla.client.feature.core.components.buttons

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import uz.yalla.client.feature.core.design.theme.YallaTheme

@Composable
fun YButton(
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    contentPadding: PaddingValues = PaddingValues(vertical = 16.dp, horizontal = 32.dp),
    containerColor: Color = YallaTheme.color.primary,
    contentColor: Color = YallaTheme.color.white,
    leadingIcon: Painter? = null,
    trailingIcon: Painter? = null,
    itemArrangement: Arrangement.Horizontal = Arrangement.spacedBy(6.dp),
    onClick: () -> Unit,
) {

    val adjustedContentPadding by remember(text) {
        mutableStateOf(
            if (text.length > 20) PaddingValues(vertical = 6.dp, horizontal = 8.dp)
            else contentPadding
        )
    }

    Button(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        modifier = modifier,
        enabled = enabled,
        contentPadding = adjustedContentPadding,
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            disabledContainerColor = YallaTheme.color.gray2,
            contentColor = contentColor,
            disabledContentColor = YallaTheme.color.gray
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = itemArrangement
        ) {
            leadingIcon?.let { icon ->
                Icon(
                    painter = icon,
                    contentDescription = null,
                    tint = contentColor
                )
            }

            Text(
                text = text,
                color = if (enabled) contentColor else YallaTheme.color.gray,
                style = YallaTheme.font.labelLarge,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            trailingIcon?.let { icon ->
                Icon(
                    painter = icon,
                    contentDescription = null,
                    tint = contentColor
                )
            }
        }
    }
}