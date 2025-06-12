package uz.yalla.client.feature.payment.corporate_account.view

import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import uz.yalla.client.core.common.dialog.LoadingDialog
import uz.yalla.client.feature.payment.corporate_account.model.CorporateAccountActionState
import uz.yalla.client.feature.payment.corporate_account.model.CorporateAccountViewModel

@Composable
internal fun AddCompanyRoute(
    onNavigateBack: () -> Unit,
    viewModel: CorporateAccountViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var loading by remember { mutableStateOf(false) }
    val pagerState = rememberPagerState { 3 }

    LaunchedEffect(Unit) {
        launch(Dispatchers.Main) {
            viewModel.actionState.collectLatest { action ->
                loading = when (action) {
                    is CorporateAccountActionState.Error -> false
                    is CorporateAccountActionState.Loading -> true
                    is CorporateAccountActionState.Success -> false
                }
            }
        }
    }

    CorporateAccountScreen(
        uiState = uiState,
        pagerState = pagerState,
        onIntent = viewModel::processIntent,
        onNavigateBack = onNavigateBack
    )


    if (loading) LoadingDialog()
}