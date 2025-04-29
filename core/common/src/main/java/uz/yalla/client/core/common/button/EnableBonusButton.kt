package uz.yalla.client.core.common.button

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import uz.yalla.client.core.common.R
import uz.yalla.client.core.presentation.design.theme.YallaTheme

@Composable
fun EnableBonusButton(
    balance: Int,
    isBonusEnabled: Boolean,
    onSwitchChecked: (Boolean) -> Unit,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(Color.Transparent)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 20.dp,
                    vertical = 12.dp
                )
        ) {
            Image(
                painter = painterResource(R.drawable.ic_coin),
                contentDescription = null,
                modifier = Modifier.size(30.dp)
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = stringResource(R.string.pay_with_bonus),
                    color = YallaTheme.color.black,
                    style = YallaTheme.font.labelSemiBold
                )

                Text(
                    text = stringResource(R.string.bonus_account, balance.toString()),
                    color = YallaTheme.color.gray,
                    style = YallaTheme.font.body
                )
            }

            Switch(
                checked = isBonusEnabled,
                onCheckedChange = onSwitchChecked,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = YallaTheme.color.white,
                    uncheckedThumbColor = YallaTheme.color.white,
                    checkedTrackColor = YallaTheme.color.primary,
                    uncheckedTrackColor = YallaTheme.color.gray2,
                    checkedBorderColor = YallaTheme.color.primary,
                    uncheckedBorderColor = YallaTheme.color.gray2,
                )
            )
        }
    }
}