package uz.yalla.client.feature.android.payment.card_verification.view

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import uz.yalla.client.core.common.dialog.LoadingDialog
import uz.yalla.client.feature.android.payment.R
import uz.yalla.client.feature.android.payment.card_verification.model.CardVerificationActionState
import uz.yalla.client.feature.android.payment.card_verification.model.CardVerificationViewModel

@Composable
internal fun CardVerificationRoute(
    key: String,
    cardNumber: String,
    cardExpiry: String,
    onNavigateBack: () -> Unit,
    viewModel: CardVerificationViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var loading by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val errorMessage = stringResource(R.string.error_message)

    LaunchedEffect(Unit) {

        launch {
            viewModel.countDownTimer(60).collectLatest { seconds ->
                viewModel.updateUiState(
                    buttonState = seconds != 0 && uiState.code.length == 6,
                    remainingMinutes = seconds / 60,
                    remainingSeconds = seconds % 60,
                    hasRemainingTime = seconds > 0
                )
            }
        }

        launch {
            viewModel.updateUiState(
                key = key,
                cardNumber = cardNumber,
                cardExpiry = cardExpiry
            )
        }

        launch {
            viewModel.actionState.collectLatest { action ->
                loading = when (action) {
                    is CardVerificationActionState.Error -> {
                        viewModel.updateUiState(buttonState = false)

                        launch {
                            snackbarHostState.showSnackbar(
                                message = errorMessage,
                                withDismissAction = true,
                                duration = SnackbarDuration.Short
                            )
                        }
                        false
                    }

                    is CardVerificationActionState.Loading -> true
                    is CardVerificationActionState.VerificationSuccess -> {
                        onNavigateBack()
                        false
                    }

                    is CardVerificationActionState.ResendSuccess -> {
                        launch {
                            viewModel.countDownTimer(60).collectLatest { seconds ->
                                viewModel.updateUiState(
                                    buttonState = seconds != 0 && uiState.code.length == 6,
                                    remainingMinutes = seconds / 60,
                                    remainingSeconds = seconds % 60,
                                    hasRemainingTime = seconds > 0
                                )
                            }
                        }
                        false
                    }
                }
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

    if (loading) LoadingDialog()
}