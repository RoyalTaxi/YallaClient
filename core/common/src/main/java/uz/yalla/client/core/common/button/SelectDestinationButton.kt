package uz.yalla.client.core.common.button

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import uz.yalla.client.core.common.R
import uz.yalla.client.core.domain.model.Destination
import uz.yalla.client.core.presentation.design.theme.YallaTheme

@Composable
fun SelectDestinationButton(
    destinations: List<Destination>,
    modifier: Modifier = Modifier,
    leadingIcon: (@Composable () -> Unit)? = null,
    onClick: () -> Unit,
    onAddNewLocation: () -> Unit
) {
    val textMeasurer = rememberTextMeasurer()
    val density = LocalDensity.current
    val screenWidthDp = LocalConfiguration.current.screenWidthDp.dp
    val screenWidthPx = with(density) { screenWidthDp.toPx() }
    val firstTextWidth = if (destinations.isNotEmpty()) textMeasurer.measure(
        text = destinations[0].name.orEmpty(),
        style = YallaTheme.font.labelLarge
    ).size.width else 0
    val firstTakesMoreSpace = firstTextWidth > (screenWidthPx * 0.5f)
    val canFitInOneRow = remember { mutableStateOf(true) }

    Button(
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(YallaTheme.color.surface),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            leadingIcon?.invoke()

            Spacer(modifier = Modifier.width(8.dp))

            when {
                destinations.size > 2 -> {
                    Text(
                        text = stringResource(R.string.destination_count, destinations.size),
                        color = YallaTheme.color.onBackground,
                        style = YallaTheme.font.labelLarge,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                }

                destinations.size == 2 && firstTakesMoreSpace -> {

                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            modifier = Modifier.weight(.5f),
                            text = destinations[0].name.orEmpty(),
                            color = YallaTheme.color.onBackground,
                            style = YallaTheme.font.labelLarge,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        Spacer(modifier = Modifier.width(4.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(0.5f)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                tint = YallaTheme.color.primary,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = destinations[1].name.orEmpty(),
                                color = YallaTheme.color.onBackground,
                                style = YallaTheme.font.labelLarge,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                destinations.isNotEmpty() -> {
                    Layout(
                        content = {
                            Row(
                                modifier = Modifier,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                destinations.forEachIndexed { index, destination ->
                                        if (index > 0) {
                                            Icon(
                                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                                tint = YallaTheme.color.primary,
                                                contentDescription = null
                                            )
                                        }

                                        Text(
                                            text = destination.name.orEmpty(),
                                            color = YallaTheme.color.onBackground,
                                            style = YallaTheme.font.labelLarge,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                            }
                        },
                        modifier = Modifier.weight(1f),

                    ) { measurables, constraints ->
                        val placeables = measurables.map { it.measure(constraints) }

                        var totalWidth = 0
                        var canFit = true
                        var maxHeight = 0

                        placeables.forEach {
                            totalWidth += it.width
                            if (totalWidth > constraints.maxWidth) canFit = false
                            if (it.height > maxHeight) maxHeight = it.height
                        }

                        canFitInOneRow.value = canFit

                        layout(width = constraints.maxWidth, height = maxHeight) {
                            if (canFit) {
                                var xPosition = 0
                                val yCenter = maxHeight / 2
                                placeables.forEach { placeable ->
                                    val yOffset = yCenter - (placeable.height / 2)
                                    placeable.placeRelative(x = xPosition, y = yOffset)
                                    xPosition += placeable.width
                                }
                            }
                        }
                    }
                }

                else -> {
                    Text(
                        text = stringResource(R.string.where_to_go),
                        color = YallaTheme.color.gray,
                        style = YallaTheme.font.labelLarge
                    )
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            if (destinations.isNotEmpty() && destinations.size < 4) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    tint = YallaTheme.color.gray,
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = ripple(color = YallaTheme.color.onBackground),
                            onClick = onAddNewLocation
                        )
                )
            }
        }
    }
}