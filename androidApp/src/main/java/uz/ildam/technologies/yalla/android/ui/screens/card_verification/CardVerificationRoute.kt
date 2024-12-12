package uz.ildam.technologies.yalla.android.ui.screens.card_verification

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import uz.ildam.technologies.yalla.android.ui.dialogs.LoadingDialog

@Composable
fun CardVerificationRoute(
    key: String,
    cardNumber: String,
    cardExpiry: String,
    onNavigateBack: () -> Unit,
    viewModel: CardVerificationViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var loading by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {

        launch {
            viewModel.countDownTimer(120).collectLatest { seconds ->
                viewModel.updateUiState(
                    buttonState = seconds != 0 && uiState.code.length == 5,
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
                    is CardVerificationActionState.Error -> false
                    is CardVerificationActionState.Loading -> true
                    is CardVerificationActionState.VerificationSuccess -> {
                        onNavigateBack()
                        false
                    }

                    is CardVerificationActionState.ResendSuccess -> {
                        viewModel.countDownTimer(120).collectLatest { seconds ->
                            viewModel.updateUiState(
                                buttonState = seconds != 0 && uiState.code.length == 5,
                                remainingMinutes = seconds / 60,
                                remainingSeconds = seconds % 60,
                                hasRemainingTime = seconds > 0
                            )
                        }
                        false
                    }
                }
            }
        }
    }

    CardVerificationScreen(
        uiState = uiState,
        onIntent = { intent ->
            when (intent) {
                is CardVerificationIntent.NavigateBack -> onNavigateBack()
                is CardVerificationIntent.ResendCode -> viewModel.addCard()
                is CardVerificationIntent.SetCode -> {
                    if (intent.code.length <= 6 && intent.code.all { it.isDigit() })
                        viewModel.updateUiState(code = intent.code)

                    if (intent.code.length == 6 && intent.code.all { it.isDigit() })
                        viewModel.updateUiState(buttonState = true)
                }

                is CardVerificationIntent.VerifyCode -> viewModel.verifyCard()
            }
        }
    )

    if (loading) LoadingDialog()
}