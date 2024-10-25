package uz.ildam.technologies.yalla.android.ui.screens.login

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import uz.ildam.technologies.yalla.android.R
import uz.ildam.technologies.yalla.android.design.theme.YallaTheme
import uz.ildam.technologies.yalla.android.ui.components.button.YallaButton
import uz.ildam.technologies.yalla.android.ui.components.textfield.PhoneNumberTextField
import uz.ildam.technologies.yalla.android.ui.components.toolbar.YallaToolbar


@Composable
fun LoginScreen(
    uiState: LoginUIState,
    onIntent: (LoginIntent) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(YallaTheme.color.white)
            .clickable(
                onClick = { onIntent(LoginIntent.ClearFocus) },
                role = Role.Image,
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            )
            .systemBarsPadding()
            .imePadding(),
    ) {
        YallaToolbar(onClick = { onIntent(LoginIntent.NavigateBack) })

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(20.dp)
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = stringResource(id = R.string.enter_phone_number),
                color = YallaTheme.color.black,
                style = YallaTheme.font.headline
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = stringResource(id = R.string.we_send_code),
                color = YallaTheme.color.gray,
                style = YallaTheme.font.body
            )

            Spacer(modifier = Modifier.height(20.dp))

            PhoneNumberTextField(
                number = uiState.number,
                onUpdateNumber = { number -> onIntent(LoginIntent.SetNumber(number)) }
            )

            Spacer(modifier = Modifier.weight(1f))

            Spacer(modifier = Modifier.height(32.dp))

            YallaButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(id = R.string.next),
                enabled = uiState.buttonState,
                onClick = { onIntent(LoginIntent.SendCode) }
            )
        }
    }
}