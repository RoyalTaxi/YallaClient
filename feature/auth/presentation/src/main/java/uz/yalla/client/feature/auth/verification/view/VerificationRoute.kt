package uz.yalla.client.feature.auth.verification.view

import android.app.Activity
import android.content.Intent
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import uz.yalla.client.core.common.dialog.BaseDialog
import uz.yalla.client.core.common.dialog.LoadingDialog
import uz.yalla.client.feature.auth.R
import uz.yalla.client.feature.auth.verification.model.VerificationActionState
import uz.yalla.client.feature.auth.verification.model.VerificationViewModel
import uz.yalla.client.feature.auth.verification.receiver.SystemBroadcastReceiver
import uz.yalla.client.feature.auth.verification.receiver.extractCode
import uz.yalla.client.feature.auth.verification.signature.SignatureHelper

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
    val uiState by vm.uiState.collectAsStateWithLifecycle()
    val loading by vm.loading.collectAsStateWithLifecycle()
    val showErrorDialog by vm.showErrorDialog.collectAsStateWithLifecycle()
    val currentErrorMessageId by vm.currentErrorMessageId.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val smsRetriever = remember { SmsRetriever.getClient(context) }

    val smsRetrieverLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {
        it.data?.let { data ->
            if (it.resultCode != Activity.RESULT_OK) return@let
            val message = data.getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE)
            val extractedCode = extractCode(message)
            if (extractedCode.isNotEmpty()) {
                vm.updateUiState(
                    code = extractedCode,
                    buttonState = uiState.hasRemainingTime && extractedCode.length == uiState.otpLength
                )
            }
        }
    }

    SystemBroadcastReceiver(
        systemAction = SmsRetriever.SMS_RETRIEVED_ACTION,
    ) { intent ->
        val extras = intent?.extras

        @Suppress("DEPRECATION")
        val smsRetrieverStatus = extras?.get(SmsRetriever.EXTRA_STATUS) as? Status // Safe cast

        when (smsRetrieverStatus?.statusCode) {
            CommonStatusCodes.SUCCESS -> {
                val consentIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    extras.getParcelable(SmsRetriever.EXTRA_CONSENT_INTENT, Intent::class.java)
                } else {
                    @Suppress("DEPRECATION")
                    extras.getParcelable(SmsRetriever.EXTRA_CONSENT_INTENT)
                }

                if (consentIntent != null) {
                    smsRetrieverLauncher.launch(consentIntent)
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        launch(Dispatchers.Main) {
            smsRetriever.startSmsUserConsent(null)
        }

        launch(Dispatchers.Default) {
            vm.updateUiState(
                number = number,
                hasRemainingTime = expiresIn > 0,
                remainingMinutes = expiresIn / 60,
                remainingSeconds = expiresIn % 60,
                buttonState = (expiresIn > 0) && uiState.code.length == uiState.otpLength
            )
        }

        launch(Dispatchers.IO) {
            vm.countDownTimer(expiresIn).collectLatest { seconds ->
                vm.updateUiState(
                    buttonState = seconds != 0 && uiState.code.length == uiState.otpLength,
                    remainingMinutes = seconds / 60,
                    remainingSeconds = seconds % 60,
                    hasRemainingTime = seconds > 0
                )
            }
        }

        launch(Dispatchers.Main) {
            vm.actionFlow.collectLatest { action ->
                when (action) {
                    is VerificationActionState.SendSMSSuccess -> {
                        val newExpiresIn = action.data.time
                        vm.updateUiState(
                            code = "",
                            hasRemainingTime = newExpiresIn > 0,
                            remainingMinutes = newExpiresIn / 60,
                            remainingSeconds = newExpiresIn % 60
                        )

                        launch(Dispatchers.IO) {
                            vm.countDownTimer(newExpiresIn).collectLatest { seconds ->
                                vm.updateUiState(
                                    buttonState = seconds != 0 && uiState.code.length == uiState.otpLength,
                                    remainingMinutes = seconds / 60,
                                    remainingSeconds = seconds % 60,
                                    hasRemainingTime = seconds > 0
                                )
                            }
                        }
                    }

                    is VerificationActionState.VerifySuccess -> {
                        if (action.data.isClient) onClientFound()
                        else onClientNotFound(number, action.data.key)
                    }
                }
            }
        }
    }

    VerificationScreen(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        onIntent = { intent ->
            when (intent) {
                VerificationIntent.NavigateBack -> onBack()
                is VerificationIntent.ResendCode -> vm.resendAuthCode(SignatureHelper.get(context))
                is VerificationIntent.VerifyCode -> vm.verifyAuthCode()
                is VerificationIntent.SetCode -> {
                    val newCode = intent.code.filter { it.isDigit() }
                    vm.updateUiState(
                        code = newCode,
                        buttonState = uiState.hasRemainingTime && newCode.length == uiState.otpLength
                    )
                    if (newCode.length == uiState.otpLength) {
                        focusManager.clearFocus(true)
                    }
                }
            }
        }
    )

    if (showErrorDialog) {
        BaseDialog(
            title = stringResource(R.string.error),
            description = currentErrorMessageId?.let { stringResource(it) },
            actionText = stringResource(R.string.ok),
            onAction = { vm.dismissErrorDialog() },
            onDismiss = { vm.dismissErrorDialog() }
        )
    }

    if (loading) LoadingDialog()
}