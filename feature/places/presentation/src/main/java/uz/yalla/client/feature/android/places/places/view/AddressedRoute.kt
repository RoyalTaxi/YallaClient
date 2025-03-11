package uz.yalla.client.feature.android.places.places.view

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
import uz.yalla.client.core.common.dialog.LoadingDialog
import uz.yalla.client.feature.android.places.places.model.AddressesActionState
import uz.yalla.client.feature.android.places.places.model.AddressesViewModel

@Composable
internal fun AddressesRoute(
    onNavigateBack: () -> Unit,
    onClickAddress: (String, Int) -> Unit,
    onAddAddress: (String) -> Unit,
    viewModel: AddressesViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var loading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        launch { viewModel.findAllAddresses() }

        launch {
            viewModel.actionState.collectLatest { action ->
                loading = when (action) {
                    AddressesActionState.Error -> false
                    AddressesActionState.Loading -> true
                    AddressesActionState.Success -> false
                }
            }
        }
    }

    AddressesScreen(
        uiState = uiState,
        onIntent = { intent ->
            when (intent) {
                is AddressesIntent.OnAddNewAddress -> onAddAddress(intent.type.typeName)
                is AddressesIntent.OnClickAddress -> onClickAddress(intent.type.typeName, intent.id)
                is AddressesIntent.OnNavigateBack -> onNavigateBack()
            }
        }
    )

    if (loading) LoadingDialog()
}