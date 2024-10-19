package uz.ildam.technologies.yalla.android.ui.screens.verification

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalFocusManager
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun VerificationRoute(
    number: String,
    expiresIn: Int,
    onBack: () -> Unit,
    onClientFound: () -> Unit,
    onClientNotFound: (String, String) -> Unit,
    vm: VerificationViewModel = koinViewModel()
) {
    var code by remember { mutableStateOf("") }
    var remainingMinutes by remember { mutableIntStateOf(0) }
    var remainingSeconds by remember { mutableIntStateOf(0) }
    var hasRemainingTime by remember { mutableStateOf(false) }
    var buttonState by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    LaunchedEffect(Unit) {
        launch {
            vm.countDownTimer(expiresIn).collect { seconds ->
                buttonState = seconds != 0 && code.length == 5
                remainingMinutes = seconds / 60
                remainingSeconds = seconds % 60
                hasRemainingTime = (remainingMinutes + remainingSeconds) > 0
            }
        }

        launch {
            vm.events.collect {
                when (it) {
                    is VerificationEvent.Error -> buttonState = false
                    is VerificationEvent.Loading -> buttonState = false
                    is VerificationEvent.SendSMSSuccess -> {
                        launch {
                            vm.countDownTimer(it.data.time).collect { seconds ->
                                buttonState = seconds != 0 && code.length == 5
                                remainingMinutes = seconds / 60
                                remainingSeconds = seconds % 60
                                hasRemainingTime = (remainingMinutes + remainingSeconds) > 0
                            }
                        }
                    }

                    is VerificationEvent.VerifySuccess -> {
                        if (it.data.client != null) onClientFound()
                        else onClientNotFound(number, it.data.key)
                    }
                }
            }
        }
    }

    VerificationScreen(
        number = number,
        code = code,
        focusManager = focusManager,
        hasRemainingTime = hasRemainingTime,
        remainingMinutes = remainingMinutes,
        remainingSeconds = remainingSeconds,
        buttonState = buttonState,
        onUpdateCode = {
            if (it.all { char -> char.isDigit() }) code = it
            buttonState = hasRemainingTime && code.length == 5
            if (code.length == 5) focusManager.clearFocus(true)
        },
        onResend = {
            vm.resendAuthCode("998$number")
            code = ""
        },
        onVerify = { vm.verifyAuthCode("998$number", code.toInt()) },
        onBack = onBack
    )
}