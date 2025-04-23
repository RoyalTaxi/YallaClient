package uz.yalla.client.core.common.topbar

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import uz.yalla.client.core.presentation.design.theme.YallaTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CenterAlignedScrollableTopBar(
    title: String,
    colors: TopAppBarColors,
    scrollBehavior: TopAppBarScrollBehavior,
    collapsedTitleTextStyle: TextStyle,
    expandedTitleTextStyle: TextStyle,
    navigationIcon: @Composable () -> Unit,
) {
    val collapsedFraction = scrollBehavior.state.collapsedFraction
    val titleTextStyle = createTextStyleWithFraction(
        start = expandedTitleTextStyle,
        stop = collapsedTitleTextStyle,
        fraction = collapsedFraction
    )
    LargeTopAppBar(
        scrollBehavior = scrollBehavior,
        navigationIcon = navigationIcon,
        colors = colors,
        title = {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = if (collapsedFraction < 0.7f) Alignment.CenterStart else Alignment.Center
            ) {
                if (collapsedFraction < 0.7f) {
                    Text(
                        text = title,
                        color = YallaTheme.color.black,
                        style = titleTextStyle
                    )
                } else {
                    Text(
                        text = title,
                        color = YallaTheme.color.black,
                        style = titleTextStyle,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .offset(x = (-24).dp * collapsedFraction)
                    )
                }
            }
        },
    )
}

private fun createTextStyleWithFraction(
    start: TextStyle,
    stop: TextStyle,
    fraction: Float
): TextStyle {
    return TextStyle(
        color = start.color,
        fontSize = createTextUnitWithFraction(start.fontSize, stop.fontSize, fraction),
        fontWeight = stop.fontWeight,
        fontFamily = stop.fontFamily,
        letterSpacing = createTextUnitWithFraction(
            start.letterSpacing,
            stop.letterSpacing,
            fraction
        ),
        lineHeight = createTextUnitWithFraction(start.lineHeight, stop.lineHeight, fraction)
    )
}

private fun createTextUnitWithFraction(start: TextUnit, stop: TextUnit, fraction: Float): TextUnit {
    return when {
        start == TextUnit.Unspecified && stop == TextUnit.Unspecified -> TextUnit.Unspecified
        start == TextUnit.Unspecified -> stop
        stop == TextUnit.Unspecified -> start
        else -> androidx.compose.ui.unit.lerp(start, stop, fraction)
    }
}