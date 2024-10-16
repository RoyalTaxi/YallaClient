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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import java.util.Locale

data class VerificationScreen(
    private val number: String,
    private val expiresIn: Int
) : Screen {

    @Composable
    override fun Content() {
        var code by remember { mutableStateOf("") }
        var remainingMinutes by remember { mutableIntStateOf(0) }
        var remainingSeconds by remember { mutableIntStateOf(0) }
        var hasRemainingTime by remember { mutableStateOf(false) }
        var buttonState by remember { mutableStateOf(false) }
        val screenModel = koinScreenModel<VerificationModel>()
        val navigator = LocalNavigator.currentOrThrow
        val focusManager = LocalFocusManager.current

        LaunchedEffect(Unit) {
            launch {
                screenModel.countDownTimer(expiresIn).collect { seconds ->
                    buttonState = seconds != 0 && code.length == 5
                    remainingMinutes = seconds / 60
                    remainingSeconds = seconds % 60
                    hasRemainingTime = (remainingMinutes + remainingSeconds) > 0
                }
            }

            launch {
                screenModel.events.collect {
                    when (it) {
                        is VerificationEvent.Error -> buttonState = false
                        is VerificationEvent.Loading -> buttonState = false
                        is VerificationEvent.SendSMSSuccess -> screenModel.countDownTimer(it.data.time)
                        is VerificationEvent.VerifySuccess -> {
                            if (it.data.isClient.not())
                                navigator push CredentialsScreen(number, it.data.key)
                        }
                    }
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(YallaTheme.color.white)
                .systemBarsPadding()
                .imePadding()
                .clickable(
                    onClick = { focusManager.clearFocus(true) },
                    role = Role.Image,
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ),
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
                    otpText = code,
                    onOtpTextChange = {
                        if (it.all { char -> char.isDigit() }) code = it
                        buttonState = hasRemainingTime && code.length == 5
                        if (code.length == 5) focusManager.clearFocus(true)
                    }
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    color = YallaTheme.color.gray,
                    style = YallaTheme.font.body,
                    text = if (hasRemainingTime) {
                        stringResource(
                            id = R.string.resend_in,
                            String.format(Locale.US, "%d:%02d", remainingMinutes, remainingSeconds)
                        )
                    } else {
                        stringResource(id = R.string.resend)
                    },
                    modifier = Modifier
                        .then(
                            if (!hasRemainingTime) {
                                Modifier.clickable(
                                    onClick = { screenModel.resendAuthCode(number) },
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = ripple(color = YallaTheme.color.white)
                                )
                            } else Modifier
                        )
                )

                Spacer(modifier = Modifier.weight(1f))

                YallaButton(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(id = R.string.next),
                    enabled = buttonState,
                    onClick = { screenModel.verifyAuthCode("998$number", code.toInt()) }
                )
            }
        }
    }
}