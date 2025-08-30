package uz.yalla.client.feature.places.places.view

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel
import org.orbitmvi.orbit.compose.collectSideEffect
import uz.yalla.client.core.common.dialog.BaseDialog
import uz.yalla.client.core.common.dialog.LoadingDialog
import uz.yalla.client.core.common.lifecycle.MakeBridge
import uz.yalla.client.feature.places.places.intent.AddressesSideEffect
import uz.yalla.client.feature.places.places.model.AddressesViewModel
import uz.yalla.client.feature.places.places.model.onIntent
import uz.yalla.client.feature.places.places.navigation.FromAddresses
import uz.yalla.client.feature.places.presentation.R

@Composable
internal fun AddressesRoute(
    navigateTo: (FromAddresses) -> Unit,
    viewModel: AddressesViewModel = koinViewModel()
) {
    val lifecycleOwner = LocalLifecycleOwner.current

    val uiState by viewModel.container.stateFlow.collectAsStateWithLifecycle()
    val loading by viewModel.loading.collectAsStateWithLifecycle()

    val showErrorDialog by viewModel.showErrorDialog.collectAsStateWithLifecycle()
    val currentErrorMessageId by viewModel.currentErrorMessageId.collectAsStateWithLifecycle()

    lifecycleOwner.MakeBridge(viewModel)

    viewModel.collectSideEffect { effect ->
        when (effect) {
            is AddressesSideEffect.AddAddress -> {
                navigateTo(FromAddresses.AddAddress(typeName = effect.typeName))
            }

            is AddressesSideEffect.NavigateAddress -> {
                navigateTo(
                    FromAddresses.NavigateToAddress(
                        typeName = effect.typeName,
                        id = effect.id
                    )
                )
            }

            AddressesSideEffect.NavigateBack -> navigateTo(FromAddresses.NavigateBack)
        }
    }

    AddressesScreen(
        uiState = uiState,
        onIntent = viewModel::onIntent
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