package uz.yalla.client.feature.order.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import uz.yalla.client.core.domain.model.Destination
import uz.yalla.client.core.presentation.design.theme.YallaTheme
import uz.yalla.client.feature.order.presentation.R

@Composable
fun ArrangeDestinationItem(
    destination: Destination,
    modifier: Modifier = Modifier,
    onDelete: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .background(
                color = YallaTheme.color.gray2,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(horizontal = 16.dp, vertical = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .border(
                    width = 1.dp,
                    color = YallaTheme.color.gray,
                    shape = CircleShape
                )

        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            modifier = Modifier.weight(1f),
            text = destination.name.orEmpty(),
            color = YallaTheme.color.black,
            style = YallaTheme.font.labelSemiBold
        )

        Spacer(modifier = Modifier.width(8.dp))

        IconButton(
            onClick = onDelete
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = null,
                tint = YallaTheme.color.red
            )
        }

        Icon(
            painter = painterResource(R.drawable.ic_arrange),
            contentDescription = null,
            tint = YallaTheme.color.gray
        )
    }
}