package uz.yalla.client.feature.order.presentation.components.items

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import uz.yalla.client.core.presentation.design.theme.YallaTheme
import uz.yalla.client.feature.order.presentation.R
import uz.yalla.client.feature.order.domain.model.response.tarrif.GetTariffsModel

@Composable
fun TariffItem(
    tariff: GetTariffsModel.Tariff,
    isDestinationsEmpty: Boolean,
    selectedState: Boolean,
    onSelect: (Boolean) -> Unit
) {
    val textColor = if (selectedState) YallaTheme.color.white else YallaTheme.color.black
    val containerColor = if (selectedState) YallaTheme.color.primary else YallaTheme.color.gray2

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor),
        onClick = { onSelect(selectedState) }
    ) {
        Column(
            modifier = Modifier
                .widthIn(min = 120.dp)
                .padding(12.dp)
        ) {
            Text(
                text = tariff.name,
                color = textColor,
                style = YallaTheme.font.labelSemiBold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = if (isDestinationsEmpty) stringResource(
                    R.string.starting_cost,
                    tariff.cost
                ) else {
                    if (tariff.fixedType) stringResource(
                        R.string.fixed_cost,
                        tariff.fixedPrice
                    )
                    else stringResource(
                        R.string.fixed_cost,
                        "~${tariff.fixedPrice}"
                    )
                },
                color = textColor,
                style = YallaTheme.font.body
            )

            Spacer(modifier = Modifier.height(8.dp))

            AsyncImage(
                model = tariff.photo,
                contentDescription = null,
                error = painterResource(R.drawable.img_default_car),
                modifier = Modifier.height(30.dp),
                contentScale = ContentScale.FillHeight
            )
        }
    }
}