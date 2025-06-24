package uz.yalla.client.feature.places.places.view

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import uz.yalla.client.core.common.dialog.BaseDialog
import uz.yalla.client.core.common.dialog.LoadingDialog
import uz.yalla.client.feature.places.places.model.AddressesViewModel
import uz.yalla.client.feature.places.presentation.R

@Composable
internal fun AddressesRoute(
    onNavigateBack: () -> Unit,
    onClickAddress: (String, Int) -> Unit,
    onAddAddress: (String) -> Unit,
    viewModel: AddressesViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val loading by viewModel.loading.collectAsStateWithLifecycle()

    val showErrorDialog by viewModel.showErrorDialog.collectAsStateWithLifecycle()
    val currentErrorMessageId by viewModel.currentErrorMessageId.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        launch(Dispatchers.IO) {
            viewModel.findAllAddresses()
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

    if (showErrorDialog) {
        BaseDialog(
            title = stringResource(R.string.error),
            description = currentErrorMessageId?.let { stringResource(it) },
            actionText = stringResource(R.string.ok),
            onAction = { viewModel.dismissErrorDialog() },
            onDismiss = { viewModel.dismissErrorDialog() }
        )
    }

    if (loading) {
        LoadingDialog()
    }
}