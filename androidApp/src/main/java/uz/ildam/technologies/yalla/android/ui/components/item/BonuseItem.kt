package uz.ildam.technologies.yalla.android.ui.components.item

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import uz.ildam.technologies.yalla.android.R
import uz.ildam.technologies.yalla.android.design.theme.YallaTheme

@Composable
fun BonuseItem(
    bonuses: String,
    isSelected: Boolean,
    onChecked: (Boolean) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .background(
                color = YallaTheme.color.white,
            )
            .padding(horizontal = 20.dp)
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_percent),
            contentDescription = null,
            tint = YallaTheme.color.white
        )

        Spacer(modifier = Modifier.width(10.dp))

        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .background(color = YallaTheme.color.white)
        ) {
            Text(
                text = stringResource(R.string.pay_with_bonuse),
                color = YallaTheme.color.black,
                style = YallaTheme.font.labelSemiBold
            )

            Text(
                text = stringResource(R.string.bonuse_account) + ": " + bonuses,
                color = YallaTheme.color.gray,
                style = YallaTheme.font.bodySmall
            )
        }

        Spacer(modifier = Modifier.weight(1f))

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
