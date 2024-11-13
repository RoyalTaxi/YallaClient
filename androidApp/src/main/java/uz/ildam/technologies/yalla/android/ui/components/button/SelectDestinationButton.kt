package uz.ildam.technologies.yalla.android.ui.components.button

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import uz.ildam.technologies.yalla.android.R
import uz.ildam.technologies.yalla.android.design.theme.YallaTheme
import uz.ildam.technologies.yalla.android.ui.screens.map.MapUIState

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SelectDestinationButton(
    destinations: List<MapUIState.Destination>,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    onAddNewClick: () -> Unit
) {
    Button(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(YallaTheme.color.gray2),
        contentPadding = PaddingValues(16.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(
                        color = YallaTheme.color.primary,
                        shape = CircleShape
                    )
            )

            Spacer(modifier = Modifier.width(10.dp))

            FlowRow {
                Text(
                    text = destinations.firstOrNull()?.name ?: stringResource(R.string.where_to_go),
                    color = if (destinations.isEmpty()) YallaTheme.color.gray else YallaTheme.color.black,
                    style = YallaTheme.font.labelLarge,
                )

                for (destination in 1 until destinations.size) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        tint = YallaTheme.color.primary,
                        contentDescription = null
                    )

                    Text(
                        text = destinations[destination].name.orEmpty(),
                        color = YallaTheme.color.black,
                        style = YallaTheme.font.labelLarge,
                    )
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            Spacer(modifier = Modifier.weight(1f))

            IconButton(
                onClick = onAddNewClick,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    tint = YallaTheme.color.gray
                )
            }
        }
    }
}