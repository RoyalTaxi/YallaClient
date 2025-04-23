package uz.yalla.client.feature.order.presentation.components.buttons

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import uz.yalla.client.core.presentation.design.theme.YallaTheme

@Composable
fun OptionsButton(
    painter: Painter,
    modifier: Modifier = Modifier,
    size: Dp = 24.dp,
    tint: Color? = Color.Unspecified,
    onClick: () -> Unit,
    badgeText: String? = null
) {
    Button(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        onClick = onClick,
        contentPadding = PaddingValues(0.dp),
        colors = ButtonDefaults.buttonColors(YallaTheme.color.gray2)
    ) {
        Box {
            Icon(
                painter = painter,
                contentDescription = null,
                tint = tint ?: Color.Unspecified,
                modifier = Modifier
                    .size(size)
                    .align(Alignment.Center)
            )

            badgeText?.let {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .offset(x = (8).dp, y = (-8).dp)
                        .align(Alignment.TopEnd)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(YallaTheme.color.red)
                            .size(14.dp)
                            .align(Alignment.Center)
                    ) {
                        Text(
                            text = badgeText,
                            style = YallaTheme.font.body.copy(fontSize = 8.sp),
                            color = YallaTheme.color.white,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }
            }
        }
    }
}