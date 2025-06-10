package uz.yalla.client.feature.payment.add_card.view

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import uz.yalla.client.core.common.dialog.BaseDialog
import uz.yalla.client.core.common.dialog.LoadingDialog
import uz.yalla.client.feature.payment.R
import uz.yalla.client.feature.payment.add_card.model.AddCardViewModel

@Composable
internal fun AddCardRoute(
    onNavigateBack: () -> Unit,
    onNavigateNext: (key: String, cardNumber: String, cardExpiry: String) -> Unit,
    viewModel: AddCardViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val loading by viewModel.loading.collectAsState()

    val showErrorDialog by viewModel.showErrorDialog.collectAsState()
    val currentErrorMessageId by viewModel.currentErrorMessageId.collectAsState()

    LaunchedEffect(Unit) {
        launch(Dispatchers.Main) {
            viewModel.navigationChannel.collect { key ->
                onNavigateNext(key, uiState.cardNumber, uiState.cardExpiry)
            }
        }
    }

    AddCardScreen(
        uiState = uiState,
        onIntent = { intent ->
            when (intent) {
                is AddCardIntent.OnClickLinkCard -> viewModel.addCard()
                is AddCardIntent.OnClickScanCard -> {}
                is AddCardIntent.OnNavigateBack -> onNavigateBack()
                is AddCardIntent.SetCardNumber -> viewModel.setCardNumber(intent.number)
                is AddCardIntent.SetExpiryDate -> viewModel.setCardDate(intent.date)
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