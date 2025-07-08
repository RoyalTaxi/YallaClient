package uz.yalla.client.core.common.item

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import uz.yalla.client.core.common.effect.shimmerEffect
import uz.yalla.client.core.presentation.design.theme.YallaTheme

@Composable
fun FoundAddressShimmer() {
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(
            horizontal = 20.dp,
            vertical = 10.dp
        )
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(YallaTheme.color.surface)
                .shimmerEffect()
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .size(100.dp, 20.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(YallaTheme.color.surface)
                    .shimmerEffect()
            )

            Box(
                modifier = Modifier
                    .size(160.dp, 16.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(YallaTheme.color.surface)
                    .shimmerEffect()
            )
        }
    }
}