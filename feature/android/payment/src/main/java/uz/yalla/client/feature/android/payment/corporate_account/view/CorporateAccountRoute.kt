package uz.yalla.client.feature.android.payment.corporate_account.view

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.compose.koinViewModel
import uz.yalla.client.feature.android.payment.corporate_account.model.AddCompanyActionState
import uz.yalla.client.feature.android.payment.corporate_account.model.AddCompanyViewModel
import uz.yalla.client.feature.core.dialogs.LoadingDialog

internal data class Page(
    @StringRes val title: Int
)

@Composable
internal fun AddCompanyRoute(
    onNavigateBack: () -> Unit,
    viewModel: AddCompanyViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var loading by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
         viewModel.actionState.collectLatest { action ->
             loading = when (action) {
                 is AddCompanyActionState.Error -> false
                 is AddCompanyActionState.Loading -> true
                 is AddCompanyActionState.Success -> false
             }
         }
    }

    AddCompanyScreen(
        uiState = uiState,
        onIntent =
    )

    if (loading) LoadingDialog()
}