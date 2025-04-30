package uz.yalla.client.feature.order.presentation.components.buttons

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import uz.yalla.client.core.common.button.PrimaryButton
import uz.yalla.client.feature.order.presentation.R

@Composable
fun LoginButton(
    onClick: () -> Unit
) {
    PrimaryButton(
        text = stringResource(R.string.login),
        onClick = onClick
    )
}