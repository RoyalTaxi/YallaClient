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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun LoginScreen(
    uiState: LoginUIState,
    onIntent: (LoginIntent) -> Unit
) {
    Scaffold(
        modifier = Modifier.imePadding(),
        containerColor = YallaTheme.color.white,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(YallaTheme.color.white),
                title = {},
                navigationIcon = {
                    IconButton(onClick = { onIntent(LoginIntent.NavigateBack) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = null
                        )
                    }
                }
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(20.dp)
            ) {
                Spacer(modifier = Modifier.height(20.dp))

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

                PhoneNumberField(
                    number = uiState.number,
                    onUpdateNumber = { number -> onIntent(LoginIntent.SetNumber(number)) }
                )

                Spacer(modifier = Modifier.weight(1f))

                Spacer(modifier = Modifier.height(32.dp))

                PrimaryButton(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(id = R.string.next),
                    enabled = uiState.buttonState,
                    onClick = { onIntent(LoginIntent.SendCode) }
                )
            }
        }
    )
}