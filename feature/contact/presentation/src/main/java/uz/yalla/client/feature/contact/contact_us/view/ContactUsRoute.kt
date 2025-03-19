package uz.yalla.client.feature.contact.contact_us.view

import android.app.Activity
import android.content.Intent
import android.content.Intent.ACTION_DIAL
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import uz.yalla.client.core.common.dialog.LoadingDialog
import uz.yalla.client.feature.contact.contact_us.components.openBrowser
import uz.yalla.client.feature.contact.contact_us.model.ContactUsActionState
import uz.yalla.client.feature.contact.contact_us.model.ContactUsViewModel

@Composable
internal fun ContactUsRoute(
    onNavigateBack: () -> Unit,
    onClickUrl: (String, String) -> Unit,
    viewModel: ContactUsViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var loading by remember { mutableStateOf(true) }
    val context = LocalContext.current as Activity

    LaunchedEffect(Unit) {
        launch { viewModel.getConfig() }

        launch {
            viewModel.actionState.collectLatest { action ->
                loading = when (action) {
                    ContactUsActionState.Error -> false
                    ContactUsActionState.Loading -> true
                    ContactUsActionState.Success -> false
                }
            }
        }
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
                        data = Uri.parse("tel:${intent.phone}")
                    }
                    if (intentIn.resolveActivity(context.packageManager) != null) {
                        context.startActivity(intentIn)
                    }
                }
            }
        }
    )

    if (loading) LoadingDialog()
}