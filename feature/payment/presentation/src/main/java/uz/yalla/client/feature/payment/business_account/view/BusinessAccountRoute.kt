package uz.yalla.client.feature.payment.business_account.view

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import uz.yalla.client.core.common.dialog.LoadingDialog
import uz.yalla.client.feature.payment.business_account.model.BusinessAccountActionState
import uz.yalla.client.feature.payment.business_account.model.BusinessAccountViewModel

@Composable
internal fun BusinessAccountRoute(
    onNavigateBack: () -> Unit,
    onClickEmployee: () -> Unit,
    onClickAddBalance: () -> Unit,
    onAddEmployee: () -> Unit,
    viewModel: BusinessAccountViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    var loading by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        launch(Dispatchers.Main) {
            viewModel.actionState.collectLatest { action ->
                loading = when (action) {
                    BusinessAccountActionState.Error -> false
                    BusinessAccountActionState.Loading -> true
                    BusinessAccountActionState.Success -> false
                }
            }
        }
    }

    BusinessAccountScreen(
        uiState = uiState,
        onIntent = { intent ->
            when (intent) {
                BusinessAccountIntent.AddEmployee -> onAddEmployee()
                BusinessAccountIntent.OnClickEmployee -> onClickEmployee()
                BusinessAccountIntent.OnNavigateBack -> onNavigateBack()
                BusinessAccountIntent.OnClickAddBalance -> onClickAddBalance()
            }
        }
    )

    if (loading) LoadingDialog()
}