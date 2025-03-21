package uz.yalla.client.feature.auth.login.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import uz.yalla.client.core.common.button.PrimaryButton
import uz.yalla.client.core.common.field.PhoneNumberField
import uz.yalla.client.core.presentation.design.theme.YallaTheme
import uz.yalla.client.feature.auth.R
import uz.yalla.client.feature.auth.login.model.LoginUIState

@Composable
internal fun LoginScreen(
    uiState: LoginUIState,
    onIntent: (LoginIntent) -> Unit
) {
    Scaffold(
        modifier = Modifier.imePadding(),
        containerColor = YallaTheme.color.white,
        topBar = {
            LoginAppBar(onNavigateBack = { onIntent(LoginIntent.NavigateBack) })
        },
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
                    number = uiState.number,
                    setPhoneNumber = { number -> onIntent(LoginIntent.SetNumber(number)) },
                )

                Spacer(modifier = Modifier.weight(1f))

                Spacer(modifier = Modifier.height(32.dp))

                LoginFooter(
                    buttonState = uiState.buttonState,
                    onClickButton = { onIntent(LoginIntent.SendCode) }
                )
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LoginAppBar(
    onNavigateBack: () -> Unit
) {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(YallaTheme.color.white),
        title = {},
        navigationIcon = {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.ArrowBack,
                    contentDescription = null
                )
            }
        }
    )
}

@Composable
private fun LoginHeader() {
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
}

@Composable
private fun LoginContent(
    number: String = "",
    setPhoneNumber: (String) -> Unit
) {
    PhoneNumberField(
        number = number,
        onUpdateNumber = { setPhoneNumber(it) }
    )
}

@Composable
private fun LoginFooter(
    buttonState: Boolean,
    onClickButton: () -> Unit
) {
    PrimaryButton(
        modifier = Modifier.fillMaxWidth(),
        text = stringResource(id = R.string.next),
        enabled = buttonState,
        onClick = onClickButton
    )
}