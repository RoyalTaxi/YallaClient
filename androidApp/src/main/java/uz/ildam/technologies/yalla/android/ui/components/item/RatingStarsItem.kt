package uz.ildam.technologies.yalla.android.ui.components.item

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import uz.ildam.technologies.yalla.android.design.theme.YallaTheme

@Composable
fun RatingStars(
    currentRating: Float,
    onRatingChange: (Float) -> Unit,
    maxRating: Int = 5,
    modifier: Modifier = Modifier,
    starSize: Dp = 32.dp,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        for (i in 1..maxRating) {
            val isFullStar = i <= currentRating

            Icon(
                imageVector = Icons.Default.Star, // Use full star icon
                contentDescription = "Star $i",
                tint = if (isFullStar) YallaTheme.color.primary else YallaTheme.color.gray,
                modifier = Modifier
                    .size(starSize)
                    .clickable {
                        onRatingChange(i.toFloat())
                    }
            )
        }
    }
}