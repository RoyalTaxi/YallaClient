package uz.yalla.client.feature.core.components.items

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
import uz.ildam.technologies.yalla.feature.addresses.domain.model.response.AddressType
import uz.yalla.client.feature.core.R
import uz.yalla.client.feature.core.design.theme.YallaTheme
import uz.yalla.client.feature.core.sheets.search_address.SearchableAddress

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
        colors = CardDefaults.cardColors(YallaTheme.color.white)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Icon(
                painter = painterResource(
                    when (foundAddress.type) {
                        AddressType.HOME -> R.drawable.ic_home
                        AddressType.WORK -> R.drawable.ic_work
                        AddressType.OTHER -> R.drawable.ic_other
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
                    color = YallaTheme.color.black,
                    style = YallaTheme.font.labelSemiBold
                )

                if (foundAddress.addressName.isNotEmpty()) {
                    Text(
                        text = foundAddress.addressName,
                        color = YallaTheme.color.gray,
                        style = YallaTheme.font.bodySmall
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