package uz.yalla.client.feature.payment.card_verification.view

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import uz.yalla.client.core.common.dialog.BaseDialog
import uz.yalla.client.core.common.dialog.LoadingDialog
import uz.yalla.client.feature.payment.R
import uz.yalla.client.feature.payment.card_verification.model.CardVerificationViewModel

@Composable
internal fun CardVerificationRoute(
    key: String,
    cardNumber: String,
    cardExpiry: String,
    onNavigateBack: () -> Unit,
    viewModel: CardVerificationViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val loading by viewModel.loading.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val errorMessage = stringResource(R.string.error_message)

    val showErrorDialog by viewModel.showErrorDialog.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        launch(Dispatchers.Default) {
            viewModel.countDownTimer(60).collectLatest { seconds ->
                viewModel.updateUiState(
                    buttonState = seconds != 0 && uiState.code.length == 6,
                    remainingMinutes = seconds / 60,
                    remainingSeconds = seconds % 60,
                    hasRemainingTime = seconds > 0
                )
            }
        }

        launch(Dispatchers.IO) {
            viewModel.updateUiState(
                key = key,
                cardNumber = cardNumber,
                cardExpiry = cardExpiry
            )
        }

        launch(Dispatchers.IO) {
            viewModel.navigationChannel.collect {
                onNavigateBack()
            }
        }
    }

    CardVerificationScreen(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        onIntent = { intent ->
            when (intent) {
                is CardVerificationIntent.NavigateBack -> onNavigateBack()
                is CardVerificationIntent.ResendCode -> viewModel.addCard()
                is CardVerificationIntent.SetCode -> {
                    if (intent.code.length <= 6 && intent.code.all { it.isDigit() })
                        viewModel.updateUiState(
                            code = intent.code,
                            buttonState = intent.code.length == 6 && uiState.hasRemainingTime
                        )
                }

                is CardVerificationIntent.VerifyCode -> viewModel.verifyCard()
            }
        }
    )

    if (showErrorDialog) {
        viewModel.updateUiState(buttonState = false)
        BaseDialog(
            title = stringResource(R.string.error),
            description = errorMessage,
            actionText = stringResource(R.string.ok),
            onAction = { viewModel.dismissErrorDialog() },
            onDismiss = { viewModel.dismissErrorDialog() }
        )
    }

    if (loading) {
        LoadingDialog()
    }
}