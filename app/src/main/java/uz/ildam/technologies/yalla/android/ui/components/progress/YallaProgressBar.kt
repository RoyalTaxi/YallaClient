package uz.ildam.technologies.yalla.android.ui.components.progress

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import uz.ildam.technologies.yalla.android.ui.components.effect.shimmerEffect
import uz.yalla.client.feature.core.design.theme.YallaTheme

@Composable
fun YallaProgressBar(
    progress: Float,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(32.dp))
            .background(YallaTheme.color.gray2)
    ) {
        Box(
            modifier = Modifier
                .height(20.dp)
                .fillMaxWidth(progress)
                .background(color = YallaTheme.color.primary)
                .shimmerEffect(durationMillis = 1500)
        )
    }
}