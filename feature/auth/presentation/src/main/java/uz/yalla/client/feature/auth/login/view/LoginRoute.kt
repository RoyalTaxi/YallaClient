package uz.yalla.client.feature.auth.login.view

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import uz.yalla.client.core.common.dialog.BaseDialog
import uz.yalla.client.core.common.dialog.LoadingDialog
import uz.yalla.client.feature.auth.R
import uz.yalla.client.feature.auth.login.model.LoginViewModel
import uz.yalla.client.feature.auth.verification.signature.SignatureHelper

@Composable
internal fun LoginRoute(
    onBack: () -> Unit,
    onNext: (String, Int) -> Unit,
    viewModel: LoginViewModel = koinViewModel()
) {
    val focusManager = LocalFocusManager.current
    val loading by viewModel.loading.collectAsStateWithLifecycle()
    val phoneNumber by viewModel.phoneNumber.collectAsStateWithLifecycle()
    val sendCodeButtonState by viewModel.sendCodeButtonState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val showErrorDialog by viewModel.showErrorDialog.collectAsStateWithLifecycle()
    val currentErrorMessageId by viewModel.currentErrorMessageId.collectAsStateWithLifecycle()

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

    if (showErrorDialog) {
        BaseDialog(
            title = stringResource(R.string.error),
            description = currentErrorMessageId?.let { stringResource(it) },
            actionText = stringResource(R.string.ok),
            onAction = { viewModel.dismissErrorDialog() },
            onDismiss = { viewModel.dismissErrorDialog() }
        )
    }

    if (loading) {
        LoadingDialog()
    }
}