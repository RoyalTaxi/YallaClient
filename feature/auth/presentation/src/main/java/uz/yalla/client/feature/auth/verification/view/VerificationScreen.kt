package uz.yalla.client.feature.auth.verification.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import uz.yalla.client.core.common.button.PrimaryButton
import uz.yalla.client.core.presentation.design.theme.YallaTheme
import uz.yalla.client.feature.auth.R
import uz.yalla.client.feature.auth.verification.model.VerificationUIState
import uz.yalla.client.core.common.otp.OtpView
import java.util.Locale


@Composable
internal fun VerificationScreen(
    uiState: VerificationUIState,
    snackbarHostState: SnackbarHostState,
    onIntent: (VerificationIntent) -> Unit,
) {
    Scaffold(
        containerColor = YallaTheme.color.white,
        modifier = Modifier.imePadding(),
        topBar = {
            LoginAppBar(onNavigateBack = { onIntent(VerificationIntent.NavigateBack) })
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(20.dp)
            ) {
                LoginContent(
                    number = uiState.number,
                    code = uiState.code,
                    hasRemainingTime = uiState.hasRemainingTime,
                    remainingMinutes = uiState.remainingMinutes,
                    remainingSeconds = uiState.remainingSeconds,
                    sendCode = { onIntent(VerificationIntent.SetCode(it)) },
                    reSendCode = { onIntent(VerificationIntent.ResendCode(uiState.number)) }
                )

                LoginFooter(
                    modifier = Modifier.weight(1f),
                    primaryButtonState = uiState.buttonState,
                    onVerifyCode = {
                        onIntent(
                            VerificationIntent.VerifyCode(
                                uiState.number,
                                uiState.code
                            )
                        )
                    }
                )
            }
        },
        snackbarHost = {
            SnackBar(hostState = snackbarHostState)
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
private fun LoginContent(
    number: String,
    code: String,
    hasRemainingTime: Boolean,
    remainingMinutes: Int,
    remainingSeconds: Int,
    sendCode: (String) -> Unit,
    reSendCode: () -> Unit
) {
    Spacer(modifier = Modifier.height(20.dp))

    Text(
        text = stringResource(id = R.string.enter_otp),
        color = YallaTheme.color.black,
        style = YallaTheme.font.headline
    )

    Spacer(modifier = Modifier.height(20.dp))

    Text(
        text = stringResource(id = R.string.enter_otp_definition, number),
        color = YallaTheme.color.gray,
        style = YallaTheme.font.body
    )

    Spacer(modifier = Modifier.height(20.dp))

    OtpView(
        modifier = Modifier.fillMaxWidth(),
        otpText = code,
        onOtpTextChange = { sendCode(it) }
    )

    Spacer(modifier = Modifier.height(20.dp))

    Text(
        color = YallaTheme.color.gray,
        style = YallaTheme.font.body,
        text = if (hasRemainingTime) {
            stringResource(
                id = R.string.resend_in,
                String.format(
                    Locale.US,
                    "%d:%02d",
                    remainingMinutes,
                    remainingSeconds
                )
            )
        } else {
            stringResource(id = R.string.resend)
        },
        modifier = Modifier.then(
            if (!hasRemainingTime) {
                Modifier.clickable(
                    onClick = reSendCode,
                    interactionSource = remember { MutableInteractionSource() },
                    indication = ripple(color = YallaTheme.color.white)
                )
            } else Modifier
        )
    )
}

@Composable
private fun LoginFooter(
    primaryButtonState: Boolean,
    onVerifyCode: () -> Unit,
    modifier: Modifier = Modifier
) {
    Spacer(modifier = modifier)

    PrimaryButton(
        modifier = Modifier.fillMaxWidth(),
        text = stringResource(id = R.string.next),
        enabled = primaryButtonState,
        onClick = onVerifyCode
    )
}

@Composable
private fun SnackBar(
    hostState: SnackbarHostState
) {
    SnackbarHost(
        hostState = hostState,
        modifier = Modifier.imePadding(),
        snackbar = { snackbarData: SnackbarData ->
            Snackbar(
                snackbarData = snackbarData,
                containerColor = YallaTheme.color.red,
                contentColor = YallaTheme.color.white
            )
        }
    )
}