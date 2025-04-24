package uz.yalla.client.feature.notification.notifications.view

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.paging.LoadState
import app.cash.paging.compose.collectAsLazyPagingItems
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
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
    val scope = rememberCoroutineScope()

    DisposableEffect(Unit) {
        var job: Job? = null

        scope.launch { job = viewModel.getNotifications() }

        onDispose {
            job?.cancel()
        }
    }

    NotificationScreen(
        notifications = notifications,
        onIntent = { intent ->
            when (intent) {
                is NotificationsIntent.OnNavigateBack -> onNavigateBack()
                is NotificationsIntent.OnClickNotifications -> onClickNotification(intent.id)
            }
        }
    )

    if (notifications.loadState.refresh is LoadState.Loading && notifications.itemCount == 0)
        LoadingDialog()
}