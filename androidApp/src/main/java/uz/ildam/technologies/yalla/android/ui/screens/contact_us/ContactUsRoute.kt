package uz.ildam.technologies.yalla.android.ui.screens.contact_us

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import uz.ildam.technologies.yalla.android.ui.dialogs.LoadingDialog

@Composable
fun ContactUsRoute(
    onNavigateBack: () -> Unit,
    onClickUrl: (String, String) -> Unit,
    viewModel: ContactUsViewModel = koinViewModel())
{
    val uiState by viewModel.uiState.collectAsState()
    var loading by remember { mutableStateOf(true) }
    val context = LocalContext.current

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
        onIntent = {intent ->
            when (intent) {
                is ContactUsIntent.OnClickUrl -> onClickUrl(context.getString(intent.title), (intent.url))
                is ContactUsIntent.OnNavigateBack -> onNavigateBack()
            }
        }
    )

    if (loading) LoadingDialog()
}