package uz.yalla.client.feature.order.presentation.components.buttons

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import uz.yalla.client.core.common.button.PrimaryButton
import uz.yalla.client.feature.order.presentation.R

@Composable
fun RowScope.LoginButton(
    onClick: () -> Unit
) {
    PrimaryButton(
        text = stringResource(R.string.login),
        onClick = onClick,
        modifier = Modifier
            .weight(1f)
            .fillMaxHeight()
            .height(56.dp)
    )
}