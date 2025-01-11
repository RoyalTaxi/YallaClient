package uz.ildam.technologies.yalla.android.ui.screens.add_card

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.compose.koinViewModel
import uz.ildam.technologies.yalla.android.ui.dialogs.LoadingDialog

@Composable
fun AddCardRoute(
    onNavigateBack: () -> Unit,
    onNavigateNext: (key: String, cardNumber: String, cardExpiry: String) -> Unit,
    viewModel: AddCardViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    var loading by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.actionState.collectLatest { action ->
            loading = when (action) {
                is AddCardActionState.Error -> false
                is AddCardActionState.Loading -> true
                is AddCardActionState.Success -> {
                    onNavigateNext(action.key, action.cardNumber, action.cardExpiry)
                    false
                }
            }
        }
    }

    AddCardScreen(
        uiState = uiState,
        onIntent = { intent ->
            when (intent) {
                is AddCardIntent.OnClickLinkCard -> viewModel.addCard()
                is AddCardIntent.OnClickScanCard -> {}
                is AddCardIntent.OnNavigateBack -> onNavigateBack()
                is AddCardIntent.SetCardNumber -> viewModel.setCardNumber(intent.number)
                is AddCardIntent.SetExpiryDate -> viewModel.setCardDate(intent.date)
            }
        }
    )

    if (loading) LoadingDialog()
}