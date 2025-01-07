package uz.ildam.technologies.yalla.android.ui.screens.about_app

import AboutAppViewModel
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
import uz.ildam.technologies.yalla.android.ui.dialogs.LoadingDialog

@Composable
fun AboutAppRoute(
    onNavigateBack: () -> Unit,
    onClickUrl: (String, String) -> Unit,
    viewModel: AboutAppViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var loading by remember { mutableStateOf(true) }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        launch { viewModel.getConfig() }

        launch {
            viewModel.actionState.collectLatest { action ->
                loading = when (action) {
                    AboutAppActionState.Error -> false
                    AboutAppActionState.Loading -> true
                    AboutAppActionState.Success -> false
                }
            }
        }
    }

    AboutAppScreen(
        uiState = uiState,
        onIntent = { intent ->
            when (intent) {
                is AboutAppIntent.OnNavigateBack -> onNavigateBack()
                is AboutAppIntent.OnClickUrl -> onClickUrl(context.getString(intent.title), intent.url)
            }
        }
    )

    if (loading) LoadingDialog()
}