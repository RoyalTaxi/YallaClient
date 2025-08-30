package uz.yalla.client.feature.contact.view

import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.Intent.ACTION_DIAL
import android.widget.Toast
import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.net.toUri
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel
import org.orbitmvi.orbit.compose.collectSideEffect
import uz.yalla.client.core.common.dialog.BaseDialog
import uz.yalla.client.core.common.dialog.LoadingDialog
import uz.yalla.client.core.common.lifecycle.MakeBridge
import uz.yalla.client.feature.contact.R
import uz.yalla.client.feature.contact.components.openBrowser
import uz.yalla.client.feature.contact.intent.ContactUsSideEffect
import uz.yalla.client.feature.contact.model.ContactUsViewModel
import uz.yalla.client.feature.contact.model.onIntent
import uz.yalla.client.feature.contact.navigation.FromContactUs

@Composable
fun ContactUsRoute(
    navigateTo: (FromContactUs) -> Unit,
    viewModel: ContactUsViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val activity = LocalActivity.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val loading by viewModel.loading.collectAsStateWithLifecycle()
    val state by viewModel.container.stateFlow.collectAsStateWithLifecycle()

    val showErrorDialog by viewModel.showErrorDialog.collectAsStateWithLifecycle()
    val currentErrorMessageId by viewModel.currentErrorMessageId.collectAsStateWithLifecycle()

    lifecycleOwner.MakeBridge(viewModel)

    viewModel.collectSideEffect { effect ->
        when (effect) {
            ContactUsSideEffect.NavigateBack -> navigateTo(FromContactUs.NavigateBack)
            is ContactUsSideEffect.NavigateWeb -> navigateTo(
                FromContactUs.ToWeb(
                    title = context.getString(effect.title),
                    url = effect.url
                )
            )

            is ContactUsSideEffect.NavigateToEmail -> {
                activity?.openBrowser(effect.email)
            }

            is ContactUsSideEffect.NavigateToPhoneCall -> {
                val intentIn = Intent(ACTION_DIAL).apply {
                    data = "tel:${effect.phoneNumber}".toUri()
                }
                try {
                    context.startActivity(intentIn)
                } catch (e: ActivityNotFoundException) {
                    Toast.makeText(context, "No dialer app found", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    ContactUsScreen(
        uiState = state,
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