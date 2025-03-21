package uz.yalla.client.feature.payment.card_verification.view
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
import uz.yalla.client.core.common.otp.OtpView
import uz.yalla.client.core.data.local.AppPreferences
import uz.yalla.client.core.presentation.design.theme.YallaTheme
import uz.yalla.client.feature.payment.R
import uz.yalla.client.feature.payment.card_verification.model.CardVerificationUIState
import java.util.Locale

@Composable
internal fun CardVerificationScreen(
    uiState: CardVerificationUIState,
    snackbarHostState: SnackbarHostState,
    onIntent: (CardVerificationIntent) -> Unit,
) {
    Scaffold(
        modifier = Modifier.imePadding(),
        containerColor = YallaTheme.color.white,
        topBar = {
            VerificationTopBar(
                onNavigateBack = { onIntent(CardVerificationIntent.NavigateBack) }
            )
        },
        content = { paddingValues ->
            VerificationContent(
                uiState = uiState,
                onIntent = onIntent,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(20.dp)
            )
        },
        snackbarHost = {
            ErrorSnackbarHost(snackbarHostState = snackbarHostState)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun VerificationTopBar(
    onNavigateBack: () -> Unit
) {
    TopAppBar(
        title = {},
        colors = TopAppBarDefaults.topAppBarColors(YallaTheme.color.white),
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
private fun VerificationContent(
    uiState: CardVerificationUIState,
    onIntent: (CardVerificationIntent) -> Unit,
    modifier: Modifier,
) {
    Column(
        modifier = modifier
    ) {
        VerificationHeader()

        Spacer(modifier = Modifier.height(20.dp))

        VerificationDescription()

        Spacer(modifier = Modifier.height(20.dp))

        OtpView(
            modifier = Modifier.fillMaxWidth(),
            otpText = uiState.code,
            otpCount = 6,
            onOtpTextChange = { onIntent(CardVerificationIntent.SetCode(it)) }
        )

        Spacer(modifier = Modifier.height(20.dp))

        ResendCodeText(
            hasRemainingTime = uiState.hasRemainingTime,
            remainingMinutes = uiState.remainingMinutes,
            remainingSeconds = uiState.remainingSeconds,
            onResendCode = { onIntent(CardVerificationIntent.ResendCode) }
        )

        Spacer(modifier = Modifier.weight(1f))

        PrimaryButton(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(id = R.string.next),
            enabled = uiState.buttonState,
            onClick = { onIntent(CardVerificationIntent.VerifyCode) }
        )
    }
}

@Composable
private fun VerificationHeader() {
    Text(
        text = stringResource(id = R.string.enter_otp),
        color = YallaTheme.color.black,
        style = YallaTheme.font.headline
    )
}

@Composable
private fun VerificationDescription() {
    Text(
        text = stringResource(
            id = R.string.enter_otp_definition_6digits,
            AppPreferences.number
        ),
        color = YallaTheme.color.gray,
        style = YallaTheme.font.body
    )
}

@Composable
private fun ResendCodeText(
    hasRemainingTime: Boolean,
    remainingMinutes: Int,
    remainingSeconds: Int,
    onResendCode: () -> Unit
) {
    val text = if (hasRemainingTime) {
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
    }

    val modifier = if (!hasRemainingTime) {
        Modifier.clickable(
            onClick = onResendCode,
            interactionSource = remember { MutableInteractionSource() },
            indication = ripple(color = YallaTheme.color.white)
        )
    } else {
        Modifier
    }

    Text(
        text = text,
        color = YallaTheme.color.gray,
        style = YallaTheme.font.body,
        modifier = modifier
    )
}

@Composable
private fun ErrorSnackbarHost(snackbarHostState: SnackbarHostState) {
    SnackbarHost(
        hostState = snackbarHostState,
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