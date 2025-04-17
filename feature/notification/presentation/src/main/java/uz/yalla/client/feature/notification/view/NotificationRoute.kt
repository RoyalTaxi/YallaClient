package uz.yalla.client.feature.notification.view

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import org.koin.androidx.compose.koinViewModel
import uz.yalla.client.feature.notification.model.NotificationViewModel

@Composable
internal fun NotificationRoute(
    onNavigateBack: () -> Unit,
    viewModel: NotificationViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    NotificationScreen(
        uiState = uiState,
        onIntent = {intent ->
            when (intent) {
                NotificationIntent.OnNavigateBack -> onNavigateBack()
            }
        }
    )
}