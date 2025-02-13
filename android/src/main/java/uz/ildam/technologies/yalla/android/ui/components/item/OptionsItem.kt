package uz.ildam.technologies.yalla.android.ui.components.item

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
import uz.ildam.technologies.yalla.android.R
import uz.ildam.technologies.yalla.feature.order.domain.model.response.tarrif.GetTariffsModel
import uz.yalla.client.feature.core.design.theme.YallaTheme

@Composable
fun OptionsItem(
    isSelected: Boolean,
    option: GetTariffsModel.Tariff.Service,
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
                    text = stringResource(R.string.fixed_cost, option.cost),
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