package uz.ildam.technologies.yalla.android.ui.components.item

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import uz.ildam.technologies.yalla.android.design.theme.YallaTheme

@Composable
fun RatingStarsItem(
    currentRating: Int,
    maxRating: Int = 5,
    starSize: Dp = 40.dp,
    modifier: Modifier = Modifier,
    onRatingChange: (Int) -> Unit
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        for (i in 1..maxRating) {
            val isFullStar = i <= currentRating

            IconButton(
                onClick = { onRatingChange(i) },
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    modifier = Modifier.size(starSize),
                    tint = if (isFullStar) YallaTheme.color.primary else YallaTheme.color.gray,
                )
            }
        }
    }
}

@Preview
@Composable
fun Preview() {
    RatingStarsItem(
        currentRating = 4,
        maxRating = 5,
        starSize = 40.dp,
        onRatingChange = {}
    )
}