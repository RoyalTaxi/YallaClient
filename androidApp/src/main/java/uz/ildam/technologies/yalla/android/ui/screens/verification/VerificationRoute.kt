package uz.ildam.technologies.yalla.android.ui.screens.verification

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalFocusManager
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import uz.ildam.technologies.yalla.android.ui.dialogs.LoadingDialog

@Composable
internal fun VerificationRoute(
    number: String,
    expiresIn: Int,
    onBack: () -> Unit,
    onClientFound: () -> Unit,
    onClientNotFound: (String, String) -> Unit,
    vm: VerificationViewModel = koinViewModel()
) {
    val focusManager = LocalFocusManager.current
    val uiState by vm.uiState.collectAsState()
    var loading by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        launch {
            vm.updateUiState(
                number = number,
                hasRemainingTime = expiresIn > 0,
                remainingMinutes = expiresIn / 60,
                remainingSeconds = expiresIn % 60
            )
        }

        launch {
            vm.countDownTimer(expiresIn).collectLatest { seconds ->
                vm.updateUiState(
                    buttonState = seconds != 0 && uiState.code.length == 5,
                    remainingMinutes = seconds / 60,
                    remainingSeconds = seconds % 60,
                    hasRemainingTime = seconds > 0
                )
            }
        }

        launch {
            vm.actionFlow.collectLatest {
                when (it) {
                    is VerificationActionState.Error -> {
                        vm.updateUiState(buttonState = false)
                        loading = false
                    }

                    is VerificationActionState.Loading -> {
                        vm.updateUiState(buttonState = false)
                        loading = true
                    }

                    is VerificationActionState.SendSMSSuccess -> {
                        loading = false
                        vm.updateUiState(
                            code = "",
                            hasRemainingTime = expiresIn > 0,
                            remainingMinutes = expiresIn / 60,
                            remainingSeconds = expiresIn % 60
                        )

                        vm.countDownTimer(it.data.time).collectLatest { seconds ->
                            vm.updateUiState(
                                buttonState = seconds != 0 && uiState.code.length == 5,
                                remainingMinutes = seconds / 60,
                                remainingSeconds = seconds % 60,
                                hasRemainingTime = seconds > 0
                            )
                        }
                    }

                    is VerificationActionState.VerifySuccess -> {
                        if (it.data.isClient) onClientFound()
                        else onClientNotFound(number, it.data.key)
                    }
                }
            }
        }
    }

    VerificationScreen(
        uiState = uiState,
        onIntent = { intent ->
            when (intent) {
                VerificationIntent.ClearFocus -> focusManager.clearFocus(true)
                VerificationIntent.NavigateBack -> onBack()
                is VerificationIntent.ResendCode -> vm.resendAuthCode()
                is VerificationIntent.VerifyCode -> vm.verifyAuthCode()
                is VerificationIntent.SetCode -> {
                    if (intent.code.all { char -> char.isDigit() }) vm.updateUiState(code = intent.code)
                    vm.updateUiState(buttonState = uiState.hasRemainingTime && intent.code.length == 5)
                    if (intent.code.length == 5) focusManager.clearFocus(true)
                }
            }
        }
    )

    if (loading) LoadingDialog()
}