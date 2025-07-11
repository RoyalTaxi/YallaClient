package uz.yalla.client.core.common.marker

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import uz.yalla.client.core.common.utils.rememberGoogleMarker
import uz.yalla.client.core.common.utils.rememberLibreMarker
import uz.yalla.client.core.presentation.design.theme.YallaTheme

@Composable
fun rememberGoogleMarkerWithInfo(
    key: String? = null,
    title: String?,
    description: String?,
    infoColor: Color,
    pointColor: Color,
    pointBackgroundColor: Color
) = rememberGoogleMarker(key) {
    InfoMarker(
        title = title,
        description = description,
        infoColor = infoColor,
        pointColor = pointColor,
        pointBackgroundColor = pointBackgroundColor
    )
}

@Composable
fun rememberLibreMarkerWithInfo(
    key: String? = null,
    title: String?,
    description: String?,
    infoColor: Color,
    pointColor: Color,
    pointBackgroundColor: Color
) = rememberLibreMarker(key) {
    InfoMarker(
        title = title,
        description = description,
        infoColor = infoColor,
        pointColor = pointColor,
        pointBackgroundColor = pointBackgroundColor
    )
}

@Composable
private fun InfoMarker(
    title: String?,
    description: String?,
    infoColor: Color,
    pointColor: Color,
    pointBackgroundColor: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        if (title != null && description != null) Card(
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(infoColor),
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(2.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp)
            ) {
                Text(
                    text = description,
                    style = YallaTheme.font.bodySmall,
                    color = YallaTheme.color.onPrimary.copy(alpha = 0.5f)
                )

                Text(
                    text = title,
                    style = YallaTheme.font.body,
                    color = YallaTheme.color.onPrimary
                )
            }
        }

        Box(
            modifier = Modifier
                .size(16.dp)
                .clip(CircleShape)
                .background(pointBackgroundColor)
                .border(
                    shape = CircleShape,
                    width = 4.dp,
                    color = pointColor
                )
        )
    }
}