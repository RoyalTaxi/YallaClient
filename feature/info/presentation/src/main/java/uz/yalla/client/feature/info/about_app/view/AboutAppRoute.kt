package uz.yalla.client.feature.info.about_app.view

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel
import org.orbitmvi.orbit.compose.collectSideEffect
import uz.yalla.client.core.common.dialog.BaseDialog
import uz.yalla.client.core.common.dialog.LoadingDialog
import uz.yalla.client.core.common.lifecycle.MakeBridge
import uz.yalla.client.feature.info.R
import uz.yalla.client.feature.info.about_app.intent.AboutAppSideEffect
import uz.yalla.client.feature.info.about_app.model.AboutAppViewModel
import uz.yalla.client.feature.info.about_app.model.onIntent
import uz.yalla.client.feature.info.about_app.navigation.FromAboutApp

@Composable
fun AboutAppRoute(
    navigateTo: (FromAboutApp) -> Unit,
    viewModel: AboutAppViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val loading by viewModel.loading.collectAsStateWithLifecycle()
    val uiState by viewModel.container.stateFlow.collectAsStateWithLifecycle()

    val showErrorDialog by viewModel.showErrorDialog.collectAsStateWithLifecycle()
    val currentErrorMessageId by viewModel.currentErrorMessageId.collectAsStateWithLifecycle()

    lifecycleOwner.MakeBridge(viewModel)

    viewModel.collectSideEffect { effect ->
        when (effect) {
            AboutAppSideEffect.NavigateBack -> navigateTo(FromAboutApp.NavigateBack)
            is AboutAppSideEffect.NavigateWeb -> navigateTo(
                FromAboutApp.ToWeb(
                    title = context.getString(effect.title),
                    url = effect.url
                )
            )
        }
    }

    AboutAppScreen(
        uiState = uiState,
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