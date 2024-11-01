package uz.ildam.technologies.yalla.android.ui.components.item

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import uz.ildam.technologies.yalla.android.R
import uz.ildam.technologies.yalla.android.design.theme.YallaTheme

@Composable
fun TariffItem(
    tariff: String,
    tariffImageUrl: String,
    startingCost: Int,
    fixedCost: Int,
    fixedState: Boolean,
    selectedState: Boolean,
    onSelect: (Boolean) -> Unit
) {
    val textColor = if (selectedState) YallaTheme.color.white else YallaTheme.color.black
    val containerColor = if (selectedState) YallaTheme.color.primary else YallaTheme.color.gray2

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = containerColor,
        onClick = { onSelect(selectedState) }
    ) {
        Column(
            modifier = Modifier
                .widthIn(min = 120.dp)
                .padding(12.dp)
        ) {
            Text(
                text = tariff,
                color = textColor,
                style = YallaTheme.font.labelSemiBold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = if (fixedState) stringResource(R.string.fixed_cost, fixedCost)
                else stringResource(R.string.starting_cost, startingCost),
                color = textColor,
            )

            Spacer(modifier = Modifier.height(8.dp))


            AsyncImage(
                model = tariffImageUrl,
                contentDescription = null,
                modifier = Modifier.height(30.dp),
                contentScale = ContentScale.FillHeight
            )
        }
    }
}