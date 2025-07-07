package uz.yalla.client.core.common.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import uz.yalla.client.core.presentation.design.theme.YallaTheme

@Composable
fun LoadingDialog(
    modifier: Modifier = Modifier,
    alpha: Float = 1f
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxSize()
            .background(YallaTheme.color.background.copy(alpha = alpha))
            .pointerInput(Unit) { }
    ) {
        CircularProgressIndicator(
            color = YallaTheme.color.primary,
            strokeWidth = 5.dp,
            trackColor = YallaTheme.color.background,
        )
    }
}


@Preview
@Composable
fun Preview() {
    LoadingDialog(alpha = .3f)
}
