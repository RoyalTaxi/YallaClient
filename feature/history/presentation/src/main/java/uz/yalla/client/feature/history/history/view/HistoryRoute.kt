package uz.yalla.client.feature.history.history.view

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import app.cash.paging.compose.collectAsLazyPagingItems
import org.koin.androidx.compose.koinViewModel
import org.orbitmvi.orbit.compose.collectSideEffect
import uz.yalla.client.core.common.dialog.BaseDialog
import uz.yalla.client.core.common.dialog.LoadingDialog
import uz.yalla.client.feature.history.R
import uz.yalla.client.feature.history.history.intent.HistorySideEffect
import uz.yalla.client.feature.history.history.model.HistoryViewModel
import uz.yalla.client.feature.history.history.model.onIntent
import uz.yalla.client.feature.history.history.navigation.FromHistory

@Composable
fun HistoryRoute(
    navigateTo: (FromHistory) -> Unit,
    vm: HistoryViewModel = koinViewModel()
) {
    val orders = vm.orders.collectAsLazyPagingItems()
    val baseLoading by vm.loading.collectAsStateWithLifecycle()

    val showErrorDialog by vm.showErrorDialog.collectAsStateWithLifecycle()
    val currentErrorMessageId by vm.currentErrorMessageId.collectAsStateWithLifecycle()

    LaunchedEffect(orders.loadState) {
        val errorState = orders.loadState.refresh as? LoadState.Error
            ?: orders.loadState.append as? LoadState.Error
            ?: orders.loadState.prepend as? LoadState.Error

        errorState?.error?.let { throwable ->
            vm.handleException(throwable)
        }
    }

    vm.collectSideEffect { effect ->
        when (effect) {
            HistorySideEffect.NavigateBack -> navigateTo(FromHistory.NavigateBack)
            is HistorySideEffect.NavigateToDetails -> navigateTo(
                FromHistory.NavigateToDetails(id = effect.id)
            )
        }
    }

    val isLoading = baseLoading || orders.loadState.refresh is LoadState.Loading

    HistoryScreen(
        orders = orders,
        onIntent = vm::onIntent
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