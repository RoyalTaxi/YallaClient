package uz.yalla.client.feature.notification.show_notification.view

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import org.orbitmvi.orbit.compose.collectSideEffect
import uz.yalla.client.core.common.dialog.BaseDialog
import uz.yalla.client.core.common.dialog.LoadingDialog
import uz.yalla.client.core.common.lifecycle.MakeBridge
import uz.yalla.client.feature.notification.presentation.R
import uz.yalla.client.feature.notification.show_notification.intent.ShowNotificationSideEffect
import uz.yalla.client.feature.notification.show_notification.model.ShowNotificationViewModel
import uz.yalla.client.feature.notification.show_notification.model.onIntent
import uz.yalla.client.feature.notification.show_notification.navigation.FromShowNotification

@Composable
internal fun ShowNotificationRoute(
    id: Int,
    navigateTo: (FromShowNotification) -> Unit,
    viewModel: ShowNotificationViewModel = koinViewModel(
        parameters = { parametersOf(id) }
    )
) {
    val lifecycleOwner = LocalLifecycleOwner.current

    val loading by viewModel.loading.collectAsStateWithLifecycle()
    val state by viewModel.container.stateFlow.collectAsStateWithLifecycle()

    val showErrorDialog by viewModel.showErrorDialog.collectAsStateWithLifecycle()
    val currentErrorMessageId by viewModel.currentErrorMessageId.collectAsStateWithLifecycle()

    lifecycleOwner.MakeBridge(viewModel)

    viewModel.collectSideEffect { effect ->
        when (effect) {
            ShowNotificationSideEffect.NavigateBack -> navigateTo(FromShowNotification.NavigateBack)
        }
    }

    ShowNotificationScreen(
        state = state,
        onIntent = viewModel::onIntent
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