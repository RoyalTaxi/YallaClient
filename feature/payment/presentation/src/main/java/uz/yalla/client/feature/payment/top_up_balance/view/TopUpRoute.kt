package uz.yalla.client.feature.payment.top_up_balance.view

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel
import uz.yalla.client.feature.payment.top_up_balance.model.TopUpViewModel

@Composable
internal fun TopUpRoute(
    onNavigateBack: () -> Unit,
    viewModel: TopUpViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    TopUpScreen(
        uiState = uiState,
        onIntent = { intent ->
            when (intent) {
                is TopUpIntent.OnNavigateBack -> onNavigateBack()
                is TopUpIntent.SetValue -> viewModel.updateBalance(intent.value)
            }
        }
    )
}