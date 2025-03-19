package uz.yalla.client.feature.payment.business_account.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import uz.yalla.client.core.presentation.design.theme.YallaTheme

@Composable
fun EmployeeItem(
    name: String,
    phoneNumber: String,
    balance: String? = null,
    tripCount: String? = null,
    isSelected: Boolean = false,
    onChecked: (Boolean) -> Unit,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(YallaTheme.color.white),
        modifier = Modifier.fillMaxWidth(),
        shape = RectangleShape
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 20.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = name,
                    color = YallaTheme.color.black,
                    style = YallaTheme.font.labelSemiBold
                )

                Text(
                    text = phoneNumber,
                    color = YallaTheme.color.gray,
                    style = YallaTheme.font.body
                )
            }

            if (balance != null && tripCount != null) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = balance,
                        color = YallaTheme.color.black,
                        style = YallaTheme.font.labelSemiBold
                    )

                    Text(
                        text = tripCount,
                        color = YallaTheme.color.gray,
                        style = YallaTheme.font.body
                    )
                }
            } else {
                Switch(
                    checked = isSelected,
                    onCheckedChange = onChecked,
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
}