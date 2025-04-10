package uz.yalla.client.feature.order.presentation.components.items

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import uz.yalla.client.core.domain.model.ServiceModel
import uz.yalla.client.core.presentation.design.theme.YallaTheme
import uz.yalla.client.feature.order.presentation.R

@Composable
fun OptionsItem(
    isSelected: Boolean,
    option: ServiceModel,
    modifier: Modifier = Modifier,
    onChecked: (Boolean) -> Unit
) {
    Card(
        modifier = modifier,
        shape = RectangleShape,
        colors = CardDefaults.cardColors(YallaTheme.color.white),
        onClick = { onChecked(!isSelected) }
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 20.dp,
                    vertical = 10.dp
                )
        ) {
            Column {
                Text(
                    text = option.name,
                    color = YallaTheme.color.black,
                    style = YallaTheme.font.labelSemiBold
                )

                Text(
                    text = if (option.isPercentCost) stringResource(
                        R.string.fixed_percent,
                        option.cost.toString()
                    ) else stringResource(
                        R.string.fixed_cost,
                        option.cost.toString()
                    ),
                    color = YallaTheme.color.gray,
                    style = YallaTheme.font.bodySmall
                )
            }

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