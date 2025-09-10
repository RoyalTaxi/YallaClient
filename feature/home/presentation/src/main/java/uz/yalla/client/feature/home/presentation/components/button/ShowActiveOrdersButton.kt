package uz.yalla.client.feature.home.presentation.components.button

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import uz.yalla.client.core.presentation.design.theme.YallaTheme
import uz.yalla.client.feature.home.presentation.R

@Composable
fun ShowActiveOrdersButton(
    modifier: Modifier = Modifier,
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
            color = YallaTheme.color.onPrimary,
            style = YallaTheme.font.labelSemiBold
        )
    }
}