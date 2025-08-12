package uz.yalla.client.feature.auth.login.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import uz.yalla.client.core.common.button.PrimaryButton
import uz.yalla.client.core.common.field.PhoneNumberField
import uz.yalla.client.core.presentation.design.theme.YallaTheme
import uz.yalla.client.feature.auth.R
import uz.yalla.client.feature.auth.login.intent.LoginIntent
import uz.yalla.client.feature.auth.login.intent.LoginState
import uz.yalla.client.feature.auth.verification.signature.SignatureHelper

@Composable
internal fun LoginScreen(
    state: LoginState,
    onIntent: (LoginIntent) -> Unit
) {
    Scaffold(
        modifier = Modifier.imePadding(),
        containerColor = YallaTheme.color.background,
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(20.dp)
            ) {
                Spacer(modifier = Modifier.height(20.dp))

                LoginHeader()

                Spacer(modifier = Modifier.height(20.dp))

                LoginContent(
                    state = state,
                    onIntent = onIntent,
                )

                Spacer(modifier = Modifier.weight(1f))

                Spacer(modifier = Modifier.height(32.dp))

                LoginFooter(
                    state = state,
                    onIntent = onIntent,
                )
            }
        }
    )
}

@Composable
private fun LoginHeader() {
    Text(
        text = stringResource(id = R.string.enter_phone_number),
        color = YallaTheme.color.onBackground,
        style = YallaTheme.font.headline
    )

    Spacer(modifier = Modifier.height(20.dp))

    Text(
        text = stringResource(id = R.string.we_send_code),
        color = YallaTheme.color.gray,
        style = YallaTheme.font.body
    )
}

@Composable
private fun LoginContent(
    state: LoginState,
    onIntent: (LoginIntent) -> Unit,
) {
    PhoneNumberField(
        number = state.phoneNumber,
        onUpdateNumber = { onIntent(LoginIntent.UpdatePhoneNumber(it)) }
    )
}

@Composable
private fun LoginFooter(
    state: LoginState,
    onIntent: (LoginIntent) -> Unit,
) {
    val context = LocalContext.current
    PrimaryButton(
        modifier = Modifier.fillMaxWidth(),
        text = stringResource(id = R.string.next),
        enabled = state.sendButtonState,
        onClick = { onIntent(LoginIntent.SendCode(SignatureHelper.get(context).orEmpty())) }
    )
}