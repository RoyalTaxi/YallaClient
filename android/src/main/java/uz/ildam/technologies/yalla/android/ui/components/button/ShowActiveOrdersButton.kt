package uz.ildam.technologies.yalla.android.ui.components.button

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import uz.ildam.technologies.yalla.android.R
import uz.yalla.client.feature.core.design.theme.YallaTheme

@Composable
fun ShowActiveOrdersButton(
    modifier: Modifier = Modifier,
    isOpen: Boolean,
    orderCount: Int,
    onClick: () -> Unit
) {
    Button(
        modifier = modifier,
        shape = RoundedCornerShape(30.dp),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 5.dp),
        colors = ButtonDefaults.buttonColors(YallaTheme.color.primary),
        onClick = onClick
    ) {
        Text(
            text = stringResource(R.string.x_order, orderCount),
            color = YallaTheme.color.white,
            style = YallaTheme.font.labelSemiBold
        )

        Icon(
            imageVector = if (isOpen) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowUp,
            contentDescription = null,
            tint = YallaTheme.color.white,
            modifier = Modifier.size(24.dp)
        )
    }
}