package uz.yalla.client.feature.contact.view

import android.app.Activity
import android.content.Intent
import android.content.Intent.ACTION_DIAL
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import uz.yalla.client.core.common.dialog.LoadingDialog
import uz.yalla.client.feature.contact.components.openBrowser
import uz.yalla.client.feature.contact.model.ContactUsActionState
import uz.yalla.client.feature.contact.model.ContactUsViewModel

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
        launch(Dispatchers.IO) { viewModel.getConfig() }

        launch(Dispatchers.Main) {
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
                        data = "tel:${intent.phone}".toUri()
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