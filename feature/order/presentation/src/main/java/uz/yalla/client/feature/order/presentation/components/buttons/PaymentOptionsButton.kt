package uz.yalla.client.feature.order.presentation.components.buttons

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import uz.yalla.client.core.presentation.design.theme.YallaTheme

@Composable
fun PaymentOptionsButton(
    painter: Painter,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    cardLastNumber: String? = null
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
                tint = Color.Unspecified,
                modifier = Modifier.size(36.dp)
            )

            cardLastNumber?.let {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(YallaTheme.color.black)
                            .padding(2.dp)
                            .align(Alignment.TopEnd)
                    ) {
                        Text(
                            text = it,
                            style = YallaTheme.font.body.copy(
                                fontSize = 8.sp,
                                fontWeight = FontWeight.W700,
                            ),
                            color = YallaTheme.color.white,
                        )
                    }
                }
            }
        }
    }
}