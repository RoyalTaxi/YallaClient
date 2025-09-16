package uz.yalla.client.feature.home.presentation.components.item

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import uz.yalla.client.core.presentation.design.theme.YallaTheme

@Composable
fun DrawerItem(
    title: String,
    painter: Painter,
    modifier: Modifier = Modifier,
    description: String? = null,
    tintColor: Color = YallaTheme.color.onBackground,
    trailingIcon: @Composable () -> Unit = {},
    onClick: () -> Unit
) {
    Card(
        modifier = modifier,
        onClick = onClick,
        colors = CardDefaults.cardColors(YallaTheme.color.background)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(
                vertical = 18.dp,
                horizontal = 20.dp
            )
        ) {
            Icon(
                painter = painter,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = tintColor
            )

            Spacer(modifier = Modifier.width(10.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = title,
                    color = YallaTheme.color.onBackground,
                    style = YallaTheme.font.labelSemiBold
                )

                description?.let {
                    Text(
                        text = it,
                        color = YallaTheme.color.gray,
                        style = YallaTheme.font.body
                    )
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            trailingIcon()
        }
    }
}