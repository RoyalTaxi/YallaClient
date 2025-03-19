package uz.yalla.client.feature.auth.verification.view

import android.app.Activity
import android.content.Intent
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
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
    val uiState by vm.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var loading by remember { mutableStateOf(false) }
    val errorMessage = stringResource(R.string.error_message)
    val context = LocalContext.current
    val smsRetriever = remember { SmsRetriever.getClient(context) }
    val smsRetrieverLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = {
            it.data?.let { data ->
                if (it.resultCode != Activity.RESULT_OK) return@let
                val message = data.getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE)
                vm.updateUiState(code = extractCode(message))
            }
        }
    )

    SystemBroadcastReceiver(
        systemAction = SmsRetriever.SMS_RETRIEVED_ACTION,
    ) { intent ->
        val extras = intent?.extras
        val smsRetrieverStatus = extras?.get(SmsRetriever.EXTRA_STATUS) as Status

        when (smsRetrieverStatus.statusCode) {
            CommonStatusCodes.SUCCESS -> {
                val consentIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    extras.getParcelable(SmsRetriever.EXTRA_CONSENT_INTENT, Intent::class.java)
                } else {
                    extras.getParcelable(SmsRetriever.EXTRA_CONSENT_INTENT)
                }

                if (consentIntent != null) {
                    smsRetrieverLauncher.launch(consentIntent)
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        launch {
            smsRetriever.startSmsUserConsent(null)
        }

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
                        launch {
                            snackbarHostState.showSnackbar(
                                message = errorMessage,
                                withDismissAction = true,
                                duration = SnackbarDuration.Short
                            )
                        }
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

                        launch {
                            vm.countDownTimer(it.data.time).collectLatest { seconds ->
                                vm.updateUiState(
                                    buttonState = seconds != 0 && uiState.code.length == 5,
                                    remainingMinutes = seconds / 60,
                                    remainingSeconds = seconds % 60,
                                    hasRemainingTime = seconds > 0
                                )
                            }
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
        snackbarHostState = snackbarHostState,
        onIntent = { intent ->
            when (intent) {
                VerificationIntent.ClearFocus -> focusManager.clearFocus(true)
                VerificationIntent.NavigateBack -> onBack()
                is VerificationIntent.ResendCode -> vm.resendAuthCode(SignatureHelper.get(context))
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