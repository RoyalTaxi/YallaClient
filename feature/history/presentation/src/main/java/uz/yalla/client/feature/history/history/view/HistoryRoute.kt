package uz.yalla.client.feature.history.history.view

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.paging.LoadState
import app.cash.paging.compose.collectAsLazyPagingItems
import org.koin.androidx.compose.koinViewModel
import uz.yalla.client.core.common.dialog.BaseDialog
import uz.yalla.client.core.common.dialog.LoadingDialog
import uz.yalla.client.feature.history.R
import uz.yalla.client.feature.history.history.model.HistoryViewModel

@Composable
internal fun HistoryRoute(
    onBack: () -> Unit,
    onClickItem: (Int) -> Unit,
    vm: HistoryViewModel = koinViewModel()
) {

    val orders = vm.orders.collectAsLazyPagingItems()
    val baseLoading by vm.loading.collectAsState()

    val showErrorDialog by vm.showErrorDialog.collectAsState()
    val currentErrorMessageId by vm.currentErrorMessageId.collectAsState()


    LaunchedEffect(orders.loadState) {
        val errorState = orders.loadState.refresh as? LoadState.Error
            ?: orders.loadState.append as? LoadState.Error
            ?: orders.loadState.prepend as? LoadState.Error

        errorState?.error?.let { throwable ->
            vm.handleException(throwable)
        }
    }

    val isLoading = baseLoading || orders.loadState.refresh is LoadState.Loading

    HistoryScreen(
        orders = orders,
        onIntent = { intent ->
            when (intent) {
                is HistoryIntent.OnHistoryItemClick -> onClickItem(intent.id.toInt())
                is HistoryIntent.OnNavigateBack -> onBack()
            }
        }
    )

    if (isLoading) LoadingDialog()

    if (showErrorDialog) {
        BaseDialog(
            title = stringResource(R.string.error),
            description = currentErrorMessageId?.let { stringResource(it) },
            actionText = stringResource(R.string.ok),
            onAction = { vm.dismissErrorDialog() },
            onDismiss = { vm.dismissErrorDialog() }
        )
    }
}