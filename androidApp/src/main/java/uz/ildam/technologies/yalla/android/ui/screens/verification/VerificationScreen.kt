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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import uz.ildam.technologies.yalla.android.R
import uz.ildam.technologies.yalla.android.design.theme.YallaTheme
import uz.ildam.technologies.yalla.android.ui.components.button.YallaButton
import uz.ildam.technologies.yalla.android.ui.components.otp.OtpView
import uz.ildam.technologies.yalla.android.ui.components.toolbar.YallaToolbar
import java.util.Locale

@Composable
internal fun VerificationScreen(
    number: String,
    code: String,
    buttonState: Boolean,
    hasRemainingTime: Boolean,
    remainingMinutes: Int,
    remainingSeconds: Int,
    focusManager: FocusManager,
    onUpdateCode: (String) -> Unit,
    onBack: () -> Unit,
    onResend: () -> Unit,
    onVerify: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(YallaTheme.color.white)
            .systemBarsPadding()
            .imePadding()
            .verticalScroll(rememberScrollState())
            .clickable(
                onClick = { focusManager.clearFocus(true) },
                role = Role.Image,
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ),
    ) {
        YallaToolbar(onClick = onBack)

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
                onOtpTextChange = onUpdateCode
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
                                onClick = onResend,
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
                onClick = onVerify
            )
        }
    }
}
