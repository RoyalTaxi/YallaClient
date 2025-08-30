package uz.yalla.client.feature.registration.presentation.view

import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import org.orbitmvi.orbit.compose.collectSideEffect
import uz.yalla.client.core.common.dialog.BaseDialog
import uz.yalla.client.core.common.dialog.LoadingDialog
import uz.yalla.client.core.common.lifecycle.MakeBridge
import uz.yalla.client.feature.registration.presentation.R
import uz.yalla.client.feature.registration.presentation.intent.RegistrationSideEffect
import uz.yalla.client.feature.registration.presentation.model.RegistrationViewModel
import uz.yalla.client.feature.registration.presentation.model.onIntent
import uz.yalla.client.feature.registration.presentation.navigation.FromRegistration

@Composable
internal fun RegistrationRoute(
    secretKey: String,
    phoneNumber: String,
    navigateTo: (FromRegistration) -> Unit,
    viewModel: RegistrationViewModel = koinViewModel(
        parameters = { parametersOf(secretKey, phoneNumber) }
    )
) {
    val activity = LocalActivity.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val uiState by viewModel.container.stateFlow.collectAsStateWithLifecycle()
    val focusManager = LocalFocusManager.current
    val loading by viewModel.loading.collectAsStateWithLifecycle()

    val showErrorDialog by viewModel.showErrorDialog.collectAsStateWithLifecycle()
    val currentErrorMessageId by viewModel.currentErrorMessageId.collectAsStateWithLifecycle()

    lifecycleOwner.MakeBridge(viewModel)

    BackHandler { activity?.moveTaskToBack(true) }

    viewModel.collectSideEffect { effect ->
        when (effect) {
            RegistrationSideEffect.NavigateBack -> navigateTo(FromRegistration.NavigateBack)
            RegistrationSideEffect.NavigateToMap -> navigateTo(FromRegistration.NavigateMap)
            RegistrationSideEffect.ClearFocus -> focusManager.clearFocus(true)
        }
    }

    RegistrationScreen(
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