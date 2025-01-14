package uz.ildam.technologies.yalla.android.ui.dialogs

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import uz.ildam.technologies.yalla.android.R
import uz.ildam.technologies.yalla.android.design.theme.YallaTheme

@Composable
fun LoadingDialog(
    modifier: Modifier = Modifier
) {
    val transition = rememberInfiniteTransition("loading")
    val degrees by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000),
            repeatMode = RepeatMode.Restart
        ), label = "loading"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxSize()
            .background(YallaTheme.color.black.copy(.6f))
            .pointerInput(Unit) { }
    ) {
        Image(
            painter = painterResource(R.drawable.ic_loading),
            contentDescription = null,
            modifier = Modifier.graphicsLayer {
                rotationZ = degrees
            }
        )
    }
}