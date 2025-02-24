package uz.ildam.technologies.yalla.android.ui.components.item

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import uz.ildam.technologies.yalla.android.R
import uz.yalla.client.feature.core.design.theme.YallaTheme

@Composable
fun LocationItem(
    location: String? = null,
    isFirst: Boolean,
    isLast: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = modifier
            .background(
                color = YallaTheme.color.gray2,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(16.dp)
    ) {
        when {
            isFirst -> Box(
                Modifier
                    .size(8.dp)
                    .border(
                        color = YallaTheme.color.gray,
                        shape = CircleShape,
                        width = 1.dp
                    )
            )

            isLast -> Box(
                Modifier
                    .size(8.dp)
                    .background(
                        shape = CircleShape,
                        color = YallaTheme.color.primary
                    )
            )

            else -> Box(
                Modifier
                    .size(8.dp)
                    .background(
                        shape = CircleShape,
                        color = YallaTheme.color.gray
                    )
            )
        }

        Text(
            text = location ?: stringResource(R.string.enter_the_address),
            color = if (location.isNullOrEmpty()) YallaTheme.color.gray else YallaTheme.color.black,
            style = YallaTheme.font.labelSemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}