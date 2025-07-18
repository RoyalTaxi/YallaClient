package uz.yalla.client.feature.order.presentation.cancel_reason.view

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel
import uz.yalla.client.core.common.dialog.LoadingDialog
import uz.yalla.client.feature.order.presentation.cancel_reason.model.CancelReasonActionState
import uz.yalla.client.feature.order.presentation.cancel_reason.model.CancelReasonViewModel

@Composable
fun CancelReasonRoute(
    orderId: Int,
    onNavigateBack: () -> Unit,
    viewModel: CancelReasonViewModel = koinViewModel()
) {
    var loading by remember { mutableStateOf(false) }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = true) {
        viewModel.actionState.collect { action ->
            loading = when (action) {
                is CancelReasonActionState.Error -> {
                    // Optionally show an error message here
                    onNavigateBack()
                    false
                }
                CancelReasonActionState.GettingSuccess -> false
                CancelReasonActionState.Loading -> true
                CancelReasonActionState.SettingSuccess -> {
                    onNavigateBack()
                    false
                }
            }
        }
    }

    CancelReasonScreen(
        uiState = uiState,
        onIntent = { intent ->
            when (intent) {
                is CancelReasonIntent.OnSelect -> viewModel.updateSelectedReason(intent.reason)
                is CancelReasonIntent.OnSelected -> viewModel.cancelReason(orderId)
                is CancelReasonIntent.NavigateBack -> onNavigateBack()
            }
        }
    )

    if (loading) LoadingDialog()
}