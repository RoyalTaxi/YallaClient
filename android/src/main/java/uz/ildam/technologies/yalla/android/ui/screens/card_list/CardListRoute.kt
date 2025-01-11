package uz.ildam.technologies.yalla.android.ui.screens.card_list

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
fun CardListRoute(
    onNavigateBack: () -> Unit,
    onAddNewCard: () -> Unit,
    viewModel: CardListViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var loading by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {

        launch { viewModel.getCardList() }

        launch {
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
            }
        }
    )

    if (loading) LoadingDialog()
}