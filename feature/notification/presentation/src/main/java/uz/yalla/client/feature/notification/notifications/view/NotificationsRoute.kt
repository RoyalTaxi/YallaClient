package uz.yalla.client.feature.notification.notifications.view

import androidx.compose.runtime.Composable
import androidx.paging.LoadState
import app.cash.paging.compose.collectAsLazyPagingItems
import org.koin.androidx.compose.koinViewModel
import uz.yalla.client.core.common.dialog.LoadingDialog
import uz.yalla.client.feature.notification.notifications.model.NotificationsViewModel

@Composable
internal fun NotificationRoute(
    onNavigateBack: () -> Unit,
    onClickNotification: (Int) -> Unit,
    viewModel: NotificationsViewModel = koinViewModel()
) {

    val notifications = viewModel.notifications.collectAsLazyPagingItems()
    val isLoading = notifications.loadState.refresh is LoadState.Loading

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
}