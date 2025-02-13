package uz.ildam.technologies.yalla.android.ui.components.button

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import uz.yalla.client.feature.core.design.theme.YallaTheme

@Composable
fun MapButton(
    painter: Painter,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Button(
        modifier = modifier,
        shape = CircleShape,
        contentPadding = PaddingValues(18.dp),
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = YallaTheme.color.white
        )
    ) {
        Icon(
            painter = painter,
            tint = YallaTheme.color.black,
            contentDescription = null
        )
    }
}