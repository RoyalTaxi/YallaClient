package uz.ildam.technologies.yalla.android.ui.components.button

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import uz.ildam.technologies.yalla.android.design.theme.YallaTheme

@Composable
fun OptionsButton(
    painter: Painter,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Button(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        onClick = onClick,
        contentPadding = PaddingValues(10.dp),
        colors = ButtonDefaults.buttonColors(YallaTheme.color.gray2)
    ) {
        Image(
            painter = painter,
            contentDescription = null
        )
    }
}