package uz.ildam.technologies.yalla.android.ui.components.item

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import uz.ildam.technologies.yalla.android.R
import uz.ildam.technologies.yalla.android.design.theme.YallaTheme
import uz.ildam.technologies.yalla.feature.order.domain.model.response.tarrif.GetTariffsModel

@Composable
fun OptionsItem(
    isSelected: Boolean,
    option: GetTariffsModel.Tariff.Service,
    modifier: Modifier = Modifier,
    onChecked: (Boolean) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth()
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