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
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import uz.yalla.client.core.common.R
import uz.yalla.client.core.presentation.design.theme.YallaTheme

@Composable
fun BonusBalanceItem(
    balance: String,
    enabled: Boolean = false,
    onClickBalance: () -> Unit
) {
    Card(
        onClick = onClickBalance,
        colors = CardDefaults.cardColors(
            containerColor = YallaTheme.color.primary,
            disabledContainerColor = YallaTheme.color.primary
        ),
        shape = RoundedCornerShape(20.dp),
        enabled = enabled,
        modifier = Modifier.padding(horizontal = 20.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom,
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = stringResource(R.string.bonus_balance),
                    style = YallaTheme.font.label,
                    color = YallaTheme.color.white
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = balance,
                        style = YallaTheme.font.title,
                        color = YallaTheme.color.white
                    )

                    Image(
                        painter = painterResource(R.drawable.ic_coin),
                        contentDescription = null,
                        modifier = Modifier.height(20.dp)
                    )
                }
            }

            if (enabled) Icon(
                painter = painterResource(R.drawable.ic_arrow_right),
                contentDescription = null,
                tint = YallaTheme.color.white
            )
        }
    }
}