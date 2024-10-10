package uz.ildam.technologies.yalla.android.components.button

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import uz.ildam.technologies.yalla.android.R
import uz.ildam.technologies.yalla.android.design.theme.YallaTheme

@Composable
fun NextButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
            .size(60.dp)
            .background(
                color = YallaTheme.color.black,
                shape = CircleShape
            )
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_next),
            contentDescription = null,
            tint = YallaTheme.color.white,
            modifier = Modifier.size(24.dp)
        )
    }
}