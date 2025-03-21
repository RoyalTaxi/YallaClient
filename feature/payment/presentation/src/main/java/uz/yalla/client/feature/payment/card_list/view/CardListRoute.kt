package uz.yalla.client.feature.payment.card_list.view

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
import uz.yalla.client.feature.payment.card_list.model.CardListActionState
import uz.yalla.client.feature.payment.card_list.model.CardListViewModel

@Composable
internal fun CardListRoute(
    onNavigateBack: () -> Unit,
    onAddNewCard: () -> Unit,
    onAddCompany: () -> Unit,
    onAddBusinessAccount: () -> Unit,
    viewModel: CardListViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var loading by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {

        launch(Dispatchers.IO) {
            viewModel.getCardList()
        }

        launch(Dispatchers.Main) {
            viewModel.actionState.collectLatest { action ->
                loading = when (action) {
                    CardListActionState.Error -> false
                    CardListActionState.Loading -> true
                    CardListActionState.Success -> false
                }
            }
        }
    }

    CardListScreen(
        uiState = uiState,
        onNavigateBack = onNavigateBack,
        onSelectItem = viewModel::selectPaymentType,
        onIntent = { intent ->
            when (intent) {
                is CardListIntent.AddNewCard -> onAddNewCard()
                is CardListIntent.OnNavigateBack -> onNavigateBack()
                is CardListIntent.AddCorporateAccount -> onAddCompany()
                is CardListIntent.AddBusinessAccount -> onAddBusinessAccount()
            }
        }
    )

    if (loading) LoadingDialog()
}