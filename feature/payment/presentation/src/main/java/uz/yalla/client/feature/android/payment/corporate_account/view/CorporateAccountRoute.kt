package uz.yalla.client.feature.android.payment.corporate_account.view

import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.compose.koinViewModel
import uz.yalla.client.feature.android.payment.corporate_account.model.CorporateAccountActionState
import uz.yalla.client.feature.android.payment.corporate_account.model.CorporateAccountViewModel
import uz.yalla.client.feature.core.dialogs.LoadingDialog

@Composable
internal fun AddCompanyRoute(
    onNavigateBack: () -> Unit,
    viewModel: CorporateAccountViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var loading by remember { mutableStateOf(false) }
    val pagerState = rememberPagerState{ 3 }

    LaunchedEffect(Unit) {
        viewModel.actionState.collectLatest { action ->
            loading = when (action) {
                is CorporateAccountActionState.Error -> false
                is CorporateAccountActionState.Loading -> true
                is CorporateAccountActionState.Success -> false
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