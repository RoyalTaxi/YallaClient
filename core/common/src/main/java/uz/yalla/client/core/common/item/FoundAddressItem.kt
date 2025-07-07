package uz.yalla.client.core.common.item

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import uz.yalla.client.core.common.R
import uz.yalla.client.core.domain.model.SearchableAddress
import uz.yalla.client.core.domain.model.type.PlaceType
import uz.yalla.client.core.presentation.design.theme.YallaTheme

@Composable
fun FoundAddressItem(
    modifier: Modifier = Modifier,
    foundAddress: SearchableAddress,
    onClick: (SearchableAddress) -> Unit,
) {
    Card(
        modifier = modifier,
        shape = RectangleShape,
        onClick = { onClick(foundAddress) },
        colors = CardDefaults.cardColors(YallaTheme.color.background)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Icon(
                painter = painterResource(
                    when (foundAddress.type) {
                        PlaceType.HOME -> R.drawable.ic_home
                        PlaceType.WORK -> R.drawable.ic_work
                        PlaceType.OTHER -> R.drawable.ic_other
                    }
                ),
                contentDescription = null
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = foundAddress.name,
                    color = YallaTheme.color.onBackground,
                    style = YallaTheme.font.labelSemiBold
                )

                if (foundAddress.addressName.isNotEmpty()) {
                    Text(
                        text = foundAddress.addressName,
                        color = YallaTheme.color.gray,
                        style = YallaTheme.font.body
                    )
                }
            }

            foundAddress.distance?.let {
                Text(
                    text = "$it km",
                    color = YallaTheme.color.gray,
                    style = YallaTheme.font.label
                )
            }
        }
    }
}