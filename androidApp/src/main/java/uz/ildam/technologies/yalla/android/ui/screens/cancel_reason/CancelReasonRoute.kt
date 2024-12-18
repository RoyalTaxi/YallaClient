package uz.ildam.technologies.yalla.android.ui.screens.cancel_reason

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import uz.ildam.technologies.yalla.android.ui.dialogs.LoadingDialog

@Composable
fun CancelReasonRoute(
    onNavigateBack: () -> Unit,
    viewModel: CancelReasonViewModel = koinViewModel()
) {
    var loading by remember { mutableStateOf(true) }
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        launch { viewModel.getSetting() }
        launch {
            viewModel.actionState.collectLatest { action ->
                when (action) {
                    CancelReasonActionState.Error -> loading = false
                    CancelReasonActionState.GettingSuccess -> loading = false
                    CancelReasonActionState.Loading -> loading = true
                    CancelReasonActionState.SettingSuccess -> {
                        loading = false
                        onNavigateBack()
                    }
                }
            }
        }
    }

    CancelReasonScreen(
        uiState = uiState,
        onSelect = viewModel::cancelReason,
        onIntent = { intent ->
            when (intent) {
                is CancelReasonIntent.OnSelect -> viewModel.updateSelectedReason(intent.reason)
                is CancelReasonIntent.OnSelected -> viewModel.cancelReason()
                is CancelReasonIntent.NavigateBack -> onNavigateBack()
            }
        }
    )

    if (loading) LoadingDialog()
}