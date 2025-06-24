package uz.yalla.client.feature.payment.add_employee.model

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel
import uz.yalla.client.feature.payment.add_employee.view.AddEmployeeIntent
import uz.yalla.client.feature.payment.add_employee.view.AddEmployeeScreen

@Composable
internal fun AddEmployeeRoute(
    onNavigateBack: () -> Unit,
    viewModel: AddEmployeeViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

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