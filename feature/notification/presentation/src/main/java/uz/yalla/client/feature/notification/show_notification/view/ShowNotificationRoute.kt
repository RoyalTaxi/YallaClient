package uz.yalla.client.feature.notification.show_notification.view

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
import uz.yalla.client.feature.notification.presentation.R
import uz.yalla.client.feature.notification.show_notification.model.ShowNotificationIntent
import uz.yalla.client.feature.notification.show_notification.model.ShowNotificationViewModel

@Composable
internal fun ShowNotificationRoute(
    id: Int,
    onNavigateBack: () -> Unit,
    viewModel: ShowNotificationViewModel = koinViewModel()
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()
    val loading by viewModel.loading.collectAsStateWithLifecycle()

    val showErrorDialog by viewModel.showErrorDialog.collectAsStateWithLifecycle()
    val currentErrorMessageId by viewModel.currentErrorMessageId.collectAsStateWithLifecycle()

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

    if (showErrorDialog) {
        BaseDialog(
            title = stringResource(R.string.error),
            description = currentErrorMessageId?.let { stringResource(it) },
            actionText = stringResource(R.string.ok),
            onAction = { viewModel.dismissErrorDialog() },
            onDismiss = { viewModel.dismissErrorDialog() }
        )
    }

    if (loading) {
        LoadingDialog()
    }
}