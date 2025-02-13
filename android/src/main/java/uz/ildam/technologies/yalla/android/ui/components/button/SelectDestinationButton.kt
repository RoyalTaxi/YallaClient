package uz.ildam.technologies.yalla.android.ui.components.button

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import uz.ildam.technologies.yalla.android.R
import uz.ildam.technologies.yalla.android.ui.screens.map.MapUIState
import uz.yalla.client.feature.core.design.theme.YallaTheme

@Composable
fun SelectDestinationButton(
    destinations: List<MapUIState.Destination>,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    onAddNewLocation: () -> Unit
) {
    Button(
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(YallaTheme.color.gray2),
        contentPadding = PaddingValues(16.dp),
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .border(
                        shape = CircleShape,
                        width = 1.dp,
                        color = YallaTheme.color.gray
                    )
            )

            Spacer(modifier = Modifier.width(8.dp))

            val canFitInOneRow = remember { mutableStateOf(true) }

            if (destinations.isNotEmpty()) Layout(
                content = {
                    Row(modifier = Modifier) {
                        destinations.takeIf { it.isNotEmpty() }
                            ?.forEachIndexed { index, destination ->
                                if (index > 0) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                        tint = YallaTheme.color.primary,
                                        contentDescription = null
                                    )
                                }

                                Text(
                                    text = destination.name.orEmpty(),
                                    color = YallaTheme.color.black,
                                    style = YallaTheme.font.labelLarge,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                    }
                },
                modifier = Modifier.weight(1f)
            ) { measurables, constraints ->
                val placeables = measurables.map { it.measure(constraints) }
                var totalWidth = 0
                var canFit = true

                placeables.forEach { placeable ->
                    totalWidth += placeable.width
                    if (totalWidth > constraints.maxWidth) {
                        canFit = false
                    }
                }

                canFitInOneRow.value = canFit

                layout(width = constraints.maxWidth, height = constraints.maxHeight) {
                    if (canFit) {
                        var xPosition = 0
                        placeables.forEach { placeable ->
                            placeable.placeRelative(x = xPosition, y = 0)
                            xPosition += placeable.width
                        }
                    }
                }
            } else Text(
                text = stringResource(R.string.where_to_go),
                color = YallaTheme.color.gray,
                style = YallaTheme.font.labelLarge
            )

            Spacer(modifier = Modifier.width(10.dp))

            if (destinations.isNotEmpty()) Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                tint = YallaTheme.color.gray,
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = ripple(color = YallaTheme.color.black),
                        onClick = onAddNewLocation
                    )
            )
        }
    }
}