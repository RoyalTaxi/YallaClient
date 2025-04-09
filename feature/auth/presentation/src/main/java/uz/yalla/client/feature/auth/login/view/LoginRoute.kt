package uz.yalla.client.feature.auth.login.view

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import uz.yalla.client.core.common.dialog.LoadingDialog
import uz.yalla.client.feature.auth.login.model.LoginViewModel
import uz.yalla.client.feature.auth.verification.signature.SignatureHelper

@Composable
internal fun LoginRoute(
    onBack: () -> Unit,
    onNext: (String, Int) -> Unit,
    viewModel: LoginViewModel = koinViewModel()
) {
    val focusManager = LocalFocusManager.current
    val loading by viewModel.loading.collectAsState()
    val phoneNumber by viewModel.phoneNumber.collectAsState()
    val sendCodeButtonState by viewModel.sendCodeButtonState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        launch(Dispatchers.Main) {
            viewModel.navigationChannel.collect { time ->
                onNext(phoneNumber, time)
            }
        }
    }

    LoginScreen(
        phoneNumber = phoneNumber,
        sendCodeButtonState = sendCodeButtonState,
        onIntent = { intent ->
            when (intent) {
                is LoginIntent.ClearFocus -> focusManager.clearFocus(true)
                is LoginIntent.NavigateBack -> onBack()
                is LoginIntent.SendCode -> viewModel.sendAuthCode(SignatureHelper.get(context))
                is LoginIntent.SetNumber -> viewModel.setNumber(number = intent.number)
            }
        }
    )

    if (loading) LoadingDialog()
}