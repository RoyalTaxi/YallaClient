package uz.yalla.client.feature.notification.notifications.view

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.paging.LoadState
import app.cash.paging.compose.collectAsLazyPagingItems
import kotlinx.coroutines.Dispatchers
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
    var loading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        launch(Dispatchers.IO) {
            viewModel.getNotifications()
        }
    }

    loading = when (notifications.loadState.refresh) {
        is LoadState.Error -> {
            false
        }
        is LoadState.Loading -> {
            true
        }
        is LoadState.NotLoading -> {
            false
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

    if (loading) LoadingDialog()
}