package uz.yalla.client.core.common.item

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import uz.yalla.client.core.common.R
import uz.yalla.client.core.presentation.design.theme.YallaTheme

@Composable
fun BonusInfoItem(
    percentage: Int,
    iconId: Int,
    body: String,
    onClick: () -> Unit,
    enabled: Boolean = false,
    backgroundColor: Color = YallaTheme.color.surface,
    textColor: Color = YallaTheme.color.onBackground,
    bodyTextColor: Color = YallaTheme.color.gray,
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            contentColor = backgroundColor,
            disabledContainerColor = backgroundColor
        ),
        enabled = enabled,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier
                .padding(vertical = 20.dp, horizontal = 16.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.percentage, percentage),
                    style = YallaTheme.font.headline,
                    color = textColor
                )

                Image(
                    painter = painterResource(iconId),
                    contentDescription = null,
                    modifier = Modifier.height(30.dp)
                )
            }

            Text(
                text = body,
                style = YallaTheme.font.body,
                color = bodyTextColor
            )
        }
    }
}