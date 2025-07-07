package uz.yalla.client.feature.order.presentation.components.items

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import uz.yalla.client.core.common.effect.shimmerEffect
import uz.yalla.client.core.presentation.design.theme.YallaTheme

@Composable
fun TariffItemShimmer(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(YallaTheme.color.surface)
    ) {
        Column(
            modifier = Modifier
                .widthIn(min = 120.dp)
                .padding(12.dp)
        ) {
            Text(
                text = "Эконом",
                color = Color.Transparent,
                style = YallaTheme.font.labelSemiBold,
                modifier = Modifier.shimmerEffect()
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "от 5000 сум",
                color = Color.Transparent,
                style = YallaTheme.font.body,
                modifier = Modifier.shimmerEffect()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .size(62.dp, 30.dp)
                    .shimmerEffect()
            )
        }
    }
}