package uz.yalla.client.feature.notification.show_notification.view

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import uz.yalla.client.core.common.dialog.LoadingDialog
import uz.yalla.client.feature.notification.show_notification.model.ShowNotificationIntent
import uz.yalla.client.feature.notification.show_notification.model.ShowNotificationViewModel

@Composable
internal fun ShowNotificationRoute(
    id: Int,
    onNavigateBack: () -> Unit,
    viewModel: ShowNotificationViewModel = koinViewModel()
) {
    val state by viewModel.stateFlow.collectAsState()
    val loading by viewModel.loading.collectAsState(true)

    LaunchedEffect(Unit) {
        launch(Dispatchers.IO) {
            viewModel.getNotification(id)
        }
    }

    ShowNotificationScreen(
        state = state,
        onIntent = { intent ->
            when (intent) {
                is ShowNotificationIntent.NavigateBack -> onNavigateBack()
            }
        }
    )

    if (loading) LoadingDialog()
}