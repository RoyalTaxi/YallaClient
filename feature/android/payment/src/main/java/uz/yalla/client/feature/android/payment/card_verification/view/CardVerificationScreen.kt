package uz.yalla.client.feature.android.payment.card_verification.view
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
import uz.ildam.technologies.yalla.core.data.local.AppPreferences
import uz.yalla.client.feature.android.payment.R
import uz.yalla.client.feature.android.payment.card_verification.model.CardVerificationUIState
import uz.yalla.client.feature.core.components.buttons.YButton
import uz.yalla.client.feature.core.components.otp.OtpView
import uz.yalla.client.feature.core.design.theme.YallaTheme
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
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
            TopAppBar(
                title = {},
                colors = TopAppBarDefaults.topAppBarColors(YallaTheme.color.white),
                navigationIcon = {
                    IconButton(onClick = { onIntent(CardVerificationIntent.NavigateBack) }) {
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
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(20.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.enter_otp),
                    color = YallaTheme.color.black,
                    style = YallaTheme.font.headline
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = stringResource(
                        id = R.string.enter_otp_definition_6digits,
                        AppPreferences.number
                    ),
                    color = YallaTheme.color.gray,
                    style = YallaTheme.font.body
                )

                Spacer(modifier = Modifier.height(20.dp))

                OtpView(
                    modifier = Modifier.fillMaxWidth(),
                    otpText = uiState.code,
                    otpCount = 6,
                    onOtpTextChange = { onIntent(CardVerificationIntent.SetCode(it)) }
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    color = YallaTheme.color.gray,
                    style = YallaTheme.font.body,
                    text = if (uiState.hasRemainingTime) {
                        stringResource(
                            id = R.string.resend_in,
                            String.format(
                                Locale.US,
                                "%d:%02d",
                                uiState.remainingMinutes,
                                uiState.remainingSeconds
                            )
                        )
                    } else {
                        stringResource(id = R.string.resend)
                    },
                    modifier = Modifier.then(
                        if (!uiState.hasRemainingTime) {
                            Modifier.clickable(
                                onClick = { onIntent(CardVerificationIntent.ResendCode) },
                                interactionSource = remember { MutableInteractionSource() },
                                indication = ripple(color = YallaTheme.color.white)
                            )
                        } else Modifier
                    )
                )

                Spacer(modifier = Modifier.weight(1f))

                YButton(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(id = R.string.next),
                    enabled = uiState.buttonState,
                    onClick = { onIntent(CardVerificationIntent.VerifyCode) }
                )
            }
        },

        snackbarHost = {
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
    )
}