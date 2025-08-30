package uz.yalla.client.feature.notification.notifications.view

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
import uz.yalla.client.feature.notification.notifications.intent.NotificationsSideEffect
import uz.yalla.client.feature.notification.notifications.model.NotificationsViewModel
import uz.yalla.client.feature.notification.notifications.model.onIntent
import uz.yalla.client.feature.notification.notifications.navigation.FromNotifications
import uz.yalla.client.feature.notification.presentation.R

@Composable
internal fun NotificationRoute(
    navigateTo: (FromNotifications) -> Unit,
    viewModel: NotificationsViewModel = koinViewModel()
) {
    val notifications = viewModel.notifications.collectAsLazyPagingItems()
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

    viewModel.collectSideEffect { effect ->
        when (effect) {
            NotificationsSideEffect.NavigateBack -> navigateTo(FromNotifications.NavigateBack)
            is NotificationsSideEffect.NavigateDetails -> navigateTo(
                FromNotifications.NavigateDetails(
                    effect.id
                )
            )
        }
    }

    NotificationScreen(
        notifications = notifications,
        onIntent = viewModel::onIntent
    )

    if (notifications.loadState.refresh is LoadState.Loading) LoadingDialog()

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