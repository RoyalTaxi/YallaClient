package uz.yalla.client.core.common.button

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import uz.yalla.client.core.presentation.design.theme.YallaTheme

@Composable
fun MapButton(
    painter: Painter,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val mutableInteractionSource = remember { MutableInteractionSource() }
    val isPressing by mutableInteractionSource.collectIsPressedAsState()
    Button(
        interactionSource = mutableInteractionSource,
        modifier = modifier.size(50.dp),
        shape = CircleShape,
        contentPadding = PaddingValues(16.dp),
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            if (isPressing) YallaTheme.color.surface
            else YallaTheme.color.background
        )
    ) {
        Icon(
            painter = painter,
            tint = YallaTheme.color.onBackground,
            contentDescription = null
        )
    }
}