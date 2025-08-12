package uz.yalla.client.feature.info.about_app.view

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import uz.yalla.client.core.common.dialog.BaseDialog
import uz.yalla.client.core.common.dialog.LoadingDialog
import uz.yalla.client.feature.info.R
import uz.yalla.client.feature.info.about_app.model.AboutAppViewModel

@Composable
 fun AboutAppRoute(
    onNavigateBack: () -> Unit,
    onClickUrl: (String, String) -> Unit,
    viewModel: AboutAppViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val loading by viewModel.loading.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val showErrorDialog by viewModel.showErrorDialog.collectAsStateWithLifecycle()
    val currentErrorMessageId by viewModel.currentErrorMessageId.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        launch(Dispatchers.IO) {
            viewModel.getConfig()
        }
    }

    AboutAppScreen(
        uiState = uiState,
        onIntent = { intent ->
            when (intent) {
                is AboutAppIntent.OnNavigateBack -> onNavigateBack()
                is AboutAppIntent.OnClickUrl -> onClickUrl(
                    context.getString(intent.title),
                    intent.url
                )
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