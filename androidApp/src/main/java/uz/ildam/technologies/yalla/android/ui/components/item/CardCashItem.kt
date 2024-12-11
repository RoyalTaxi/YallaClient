package uz.ildam.technologies.yalla.android.ui.components.item

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import uz.ildam.technologies.yalla.android.design.theme.YallaTheme

@Composable
fun SelectPaymentTypeItem(
    isSelected: Boolean,
    painter: Painter,
    text: String,
    tint: Color = Color.Unspecified,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.padding(horizontal = 10.dp),
        onClick = onSelect,
        colors = CardDefaults.cardColors(YallaTheme.color.white)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 10.dp,
                    vertical = 20.dp
                )
        ) {
            Icon(
                painter = painter,
                contentDescription = null,
                tint = tint
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = text,
                color = YallaTheme.color.black,
                style = YallaTheme.font.labelSemiBold
            )

            Spacer(modifier = Modifier.weight(1f))

            if (isSelected) Icon(
                imageVector = Icons.Filled.Done,
                contentDescription = null,
                tint = YallaTheme.color.white,
                modifier = Modifier
                    .background(
                        color = YallaTheme.color.primary,
                        shape = CircleShape
                    )
                    .padding(6.dp)
            )
        }
    }
}
