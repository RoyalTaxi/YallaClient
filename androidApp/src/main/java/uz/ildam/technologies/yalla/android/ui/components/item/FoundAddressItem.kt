package uz.ildam.technologies.yalla.android.ui.components.item

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import uz.ildam.technologies.yalla.android.design.theme.YallaTheme
import uz.ildam.technologies.yalla.feature.map.domain.model.map.SearchForAddressItemModel

@Composable
fun FoundAddressItem(
    modifier: Modifier = Modifier,
    foundAddress: SearchForAddressItemModel,
    onClick: (SearchForAddressItemModel) -> Unit,
) {
    Card(
        modifier = modifier,
        onClick = { onClick(foundAddress) },
        colors = CardDefaults.cardColors(YallaTheme.color.white)
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(14.dp)
                    .border(
                        color = YallaTheme.color.gray,
                        shape = CircleShape,
                        width = 2.dp
                    )
                    .padding(10.dp)
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

                if (foundAddress.addressName.isNotEmpty()) Text(
                    text = foundAddress.addressName,
                    color = YallaTheme.color.gray,
                    style = YallaTheme.font.bodySmall
                )
            }

            Text(
                text = foundAddress.distance.toString(),
                color = YallaTheme.color.gray,
                style = YallaTheme.font.label
            )
        }
    }
}