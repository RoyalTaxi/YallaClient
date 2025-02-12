package uz.yalla.client.feature.android.payment.add_employee

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun AddEmployeeRoute(
    onNavigateBack: () -> Unit,
    viewModel: AddEmployeeViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    AddEmployeeScreen(
        uiState = uiState,
        onIntent = {intent ->
            when (intent) {
                is AddEmployeeIntent.OnNavigateBack -> onNavigateBack()
                is AddEmployeeIntent.SetFullName -> viewModel.setFullName(intent.fullName)
                is AddEmployeeIntent.SetNumber -> viewModel.setNumber(intent.number)
            }
        }
    )
}