package uz.yalla.client.feature.notification.notifications.view

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import app.cash.paging.compose.collectAsLazyPagingItems
import org.koin.androidx.compose.koinViewModel
import uz.yalla.client.core.common.dialog.BaseDialog
import uz.yalla.client.core.common.dialog.LoadingDialog
import uz.yalla.client.feature.notification.notifications.model.NotificationsViewModel
import uz.yalla.client.feature.notification.presentation.R

@Composable
internal fun NotificationRoute(
    onNavigateBack: () -> Unit,
    onClickNotification: (Int) -> Unit,
    viewModel: NotificationsViewModel = koinViewModel()
) {

    val notifications = viewModel.notifications.collectAsLazyPagingItems()
    val baseLoading by viewModel.loading.collectAsStateWithLifecycle()
    val showErrorDialog by viewModel.showErrorDialog.collectAsStateWithLifecycle()
    val currentErrorMessageId by viewModel.currentErrorMessageId.collectAsStateWithLifecycle()


    LaunchedEffect(notifications.loadState) {
        val errorState = notifications.loadState.refresh as? LoadState.Error
            ?: notifications.loadState.append as? LoadState.Error
            ?: notifications.loadState.prepend as? LoadState.Error

        errorState?.error?.let { throwable ->
            viewModel.handleException(throwable)
        }
    }

    val isLoading = baseLoading ||
            (notifications.loadState.refresh is LoadState.Loading && notifications.itemCount == 0)


    NotificationScreen(
        notifications = notifications,
        onIntent = { intent ->
            when (intent) {
                is NotificationsIntent.OnNavigateBack -> onNavigateBack()
                is NotificationsIntent.OnClickNotifications -> onClickNotification(intent.id)
            }
        }
    )

    if (isLoading) LoadingDialog()

    if (showErrorDialog) {
        BaseDialog(
            title = stringResource(R.string.error),
            description = currentErrorMessageId?.let { stringResource(it) },
            actionText = stringResource(R.string.ok),
            onAction = { viewModel.dismissErrorDialog() },
            onDismiss = { viewModel.dismissErrorDialog() }
        )
    }
}