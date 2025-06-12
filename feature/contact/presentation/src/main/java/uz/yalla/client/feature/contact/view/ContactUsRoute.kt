package uz.yalla.client.feature.contact.view

import android.app.Activity
import android.content.Intent
import android.content.Intent.ACTION_DIAL
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import uz.yalla.client.core.common.dialog.BaseDialog
import uz.yalla.client.core.common.dialog.LoadingDialog
import uz.yalla.client.feature.contact.R
import uz.yalla.client.feature.contact.components.openBrowser
import uz.yalla.client.feature.contact.model.ContactUsViewModel

@Composable
internal fun ContactUsRoute(
    onNavigateBack: () -> Unit,
    onClickUrl: (String, String) -> Unit,
    viewModel: ContactUsViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val loading by viewModel.loading.collectAsStateWithLifecycle()
    val context = LocalContext.current as Activity

    val showErrorDialog by viewModel.showErrorDialog.collectAsStateWithLifecycle()
    val currentErrorMessageId by viewModel.currentErrorMessageId.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        launch(Dispatchers.IO) { viewModel.getConfig() }
    }

    ContactUsScreen(
        uiState = uiState,
        onIntent = { intent ->
            when (intent) {
                is ContactUsIntent.OnClickUrl -> onClickUrl(
                    context.getString(intent.title), (intent.url)
                )

                is ContactUsIntent.OnNavigateBack -> onNavigateBack()
                is ContactUsIntent.OnClickEmail -> {
                    context.openBrowser(intent.email)
                }

                is ContactUsIntent.OnClickPhone -> {
                    val intentIn = Intent(ACTION_DIAL).apply {
                        data = "tel:${intent.phone}".toUri()
                    }
                    if (intentIn.resolveActivity(context.packageManager) != null) {
                        context.startActivity(intentIn)
                    }
                }
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