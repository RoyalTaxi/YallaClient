package uz.yalla.client.feature.payment.card_list.view

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import uz.yalla.client.core.common.dialog.BaseDialog
import uz.yalla.client.core.common.dialog.LoadingDialog
import uz.yalla.client.feature.payment.R
import uz.yalla.client.feature.payment.card_list.model.CardListViewModel
import uz.yalla.client.feature.payment.card_list.model.onIntent

@Composable
internal fun CardListRoute(
    onNavigateBack: () -> Unit,
    onAddNewCard: () -> Unit,
    onAddCompany: () -> Unit,
    onAddBusinessAccount: () -> Unit,
    viewModel: CardListViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val loading by viewModel.loading.collectAsStateWithLifecycle()

    val showErrorDialog by viewModel.showErrorDialog.collectAsStateWithLifecycle()
    val currentErrorMessageId by viewModel.currentErrorMessageId.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        launch(Dispatchers.IO) {
            viewModel.onIntent(CardListIntent.LoadCardList)
        }
    }

    CardListScreen(
        uiState = uiState,
        onNavigateBack = onNavigateBack,
        onSelectItem = { paymentType -> 
            viewModel.onIntent(CardListIntent.SelectPaymentType(paymentType))
        },
        onIntent = { intent ->
            when (intent) {
                is CardListIntent.AddNewCard -> onAddNewCard()
                is CardListIntent.OnNavigateBack -> onNavigateBack()
                is CardListIntent.AddCorporateAccount -> onAddCompany()
                is CardListIntent.AddBusinessAccount -> onAddBusinessAccount()
                else -> viewModel.onIntent(intent)
            }
        }
    )

    if (showErrorDialog) {
        BaseDialog(
            title = stringResource(R.string.error),
            description = currentErrorMessageId?.let { stringResource(it) },
            actionText = stringResource(R.string.ok),
            onAction = { viewModel.onIntent(CardListIntent.DismissErrorDialog) },
            onDismiss = { viewModel.onIntent(CardListIntent.DismissErrorDialog) }
        )
    }

    if (uiState.isConfirmDeleteDialogVisibility) {
        BaseDialog(
            title = stringResource(R.string.attention),
            description = stringResource(R.string.confirm_to_delete),
            dismissText = stringResource(R.string.cancel),
            actionText = stringResource(R.string.delete),
            onAction = {
                viewModel.onIntent(CardListIntent.ConfirmDeleteCard(uiState.selectedCardId))
            },
            onDismiss = { viewModel.onIntent(CardListIntent.DismissDeleteConfirmationDialog) }
        )
    }

    if (loading) {
        LoadingDialog()
    }
}
