package uz.ildam.technologies.yalla.android.ui.screens.verification

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
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import kotlinx.coroutines.launch
import uz.ildam.technologies.yalla.android.R
import uz.ildam.technologies.yalla.android.components.button.YallaButton
import uz.ildam.technologies.yalla.android.components.otp.OtpView
import uz.ildam.technologies.yalla.android.components.toolbar.YallaToolbar
import uz.ildam.technologies.yalla.android.design.theme.YallaTheme
import uz.ildam.technologies.yalla.android.ui.screens.credentials.CredentialsScreen

data class VerificationScreen(
    private val number: String,
    private val time: Int
) : Screen {

    @Composable
    override fun Content() {
        val screenModel = koinScreenModel<VerificationModel>()
        val uiState by screenModel.uiState.collectAsState()
        val navigator = LocalNavigator.currentOrThrow
        val focusManager = LocalFocusManager.current

        LaunchedEffect(Unit) {
            screenModel.startTimer(time)
            screenModel.updateNumber(number)

            launch {
                screenModel.successFlow.collect { isClient ->
                    if (!isClient) navigator replace CredentialsScreen(
                        number = uiState.getFormattedNumber(),
                        secretKey = uiState.secretKey
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(YallaTheme.color.white)
                .clickable(
                    onClick = { focusManager.clearFocus(true) },
                    role = Role.Image,
                    interactionSource = remember { MutableInteractionSource() },
                    indication = ripple(color = YallaTheme.color.white)
                )
                .systemBarsPadding()
                .imePadding(),
        ) {
            YallaToolbar(onClick = navigator::pop)

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(20.dp)
            ) {
                Spacer(modifier = Modifier.height(40.dp))

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
                    otpText = uiState.otp,
                    onOtpTextChange = {
                        screenModel.updateOtp(it)
                        if (it.length == 5) focusManager.clearFocus(true)
                    }
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = if (uiState.timerActiveState) stringResource(
                        id = R.string.resend_in,
                        uiState.resendInSecondsText
                    ) else stringResource(id = R.string.resend),
                    color = YallaTheme.color.gray,
                    style = YallaTheme.font.body,
                    modifier = if (uiState.timerActiveState.not()) Modifier.clickable(
                        onClick = screenModel::resendAuthCode,
                        interactionSource = remember { MutableInteractionSource() },
                        indication = ripple(color = YallaTheme.color.white)
                    ) else Modifier
                )


                Spacer(modifier = Modifier.weight(1f))

                YallaButton(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(id = R.string.next),
                    enabled = uiState.buttonEnabled && uiState.otp.length == 5,
                    onClick = screenModel::validateAuthCode
                )
            }
        }
    }
}