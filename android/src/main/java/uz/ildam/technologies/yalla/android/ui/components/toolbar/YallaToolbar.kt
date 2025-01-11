package uz.ildam.technologies.yalla.android.ui.components.toolbar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import uz.ildam.technologies.yalla.android.ui.components.button.BackArrowButton
import uz.ildam.technologies.yalla.android.design.theme.YallaTheme

@Composable
fun YallaToolbar(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    text: String = "",
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        BackArrowButton(onClick)

        Text(
            text = text,
            color = YallaTheme.color.black,
            style = YallaTheme.font.labelLarge,
            overflow = TextOverflow.Ellipsis
        )

        Box(modifier = Modifier.size(60.dp))
    }
}