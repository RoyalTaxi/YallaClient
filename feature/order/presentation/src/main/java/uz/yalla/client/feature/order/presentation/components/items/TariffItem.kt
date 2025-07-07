package uz.yalla.client.feature.order.presentation.components.items

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import uz.yalla.client.core.presentation.design.theme.YallaTheme
import uz.yalla.client.feature.order.domain.model.response.tarrif.AwardPaymentType
import uz.yalla.client.feature.order.domain.model.response.tarrif.GetTariffsModel
import uz.yalla.client.feature.order.presentation.R

@Composable
fun TariffItem(
    tariff: GetTariffsModel.Tariff,
    isDestinationsEmpty: Boolean,
    selectedState: Boolean,
    onSelect: (Boolean) -> Unit
) {
    val textColor = if (selectedState) YallaTheme.color.onPrimary else YallaTheme.color.onBackground
    val containerColor = if (selectedState) YallaTheme.color.primary else YallaTheme.color.surface
    val bonusPercentColor = if (selectedState) YallaTheme.color.black else YallaTheme.color.primary

    Box(
        modifier = Modifier
            .wrapContentSize()
    ) {
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

        tariff.award?.let {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .offset(x = 4.dp, y = (-4).dp)
                    .align(Alignment.TopEnd)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(bonusPercentColor)
                        .padding(vertical = 2.dp, horizontal = 4.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_coin),
                        contentDescription = null,
                        tint = Color.Unspecified,
                        modifier = Modifier
                            .size(12.dp)
                    )

                    CompositionLocalProvider(
                        LocalTextStyle provides YallaTheme.font.body,
                        LocalContentColor provides YallaTheme.color.onPrimary
                    ) {
                        when (it.type) {
                            AwardPaymentType.CASH -> {
                                Text(stringResource(R.string.bonus_added_as_cash, it.value))
                            }

                            AwardPaymentType.PERCENT -> {
                                Text(stringResource(R.string.bonus_added_as_percent, it.value))
                            }
                        }
                    }
                }
            }
        }
    }
}