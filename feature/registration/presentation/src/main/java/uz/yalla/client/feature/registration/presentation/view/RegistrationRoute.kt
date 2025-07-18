package uz.yalla.client.feature.registration.presentation.view

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import uz.yalla.client.core.common.dialog.BaseDialog
import uz.yalla.client.core.common.dialog.LoadingDialog
import uz.yalla.client.feature.registration.presentation.R
import uz.yalla.client.feature.registration.presentation.model.RegistrationViewModel

@Composable
internal fun RegistrationRoute(
    number: String,
    secretKey: String,
    onBack: () -> Unit,
    onNext: () -> Unit,
    vm: RegistrationViewModel = koinViewModel()
) {
    val uiState by vm.uiState.collectAsStateWithLifecycle()
    val focusManager = LocalFocusManager.current
    val scope = rememberCoroutineScope()
    val activity = LocalContext.current as Activity
    val loading by vm.loading.collectAsStateWithLifecycle()

    val showErrorDialog by vm.showErrorDialog.collectAsStateWithLifecycle()
    val currentErrorMessageId by vm.currentErrorMessageId.collectAsStateWithLifecycle()

    BackHandler { activity.moveTaskToBack(true) }

    LaunchedEffect(Unit) {
        launch(Dispatchers.IO) {
            vm.updateUiState(
                number = number,
                secretKey = secretKey
            )
        }

        launch(Dispatchers.Main) {
            vm.navigationChannel.collect {
                onNext()
            }
        }
    }

    RegistrationScreen(
        uiState = uiState,
        onIntent = { intent ->
            when (intent) {
                is RegistrationIntent.CloseDateBottomSheet -> scope.launch {
                    vm.setDatePickerVisible(
                        false
                    )
                }

                is RegistrationIntent.NavigateBack -> onBack()
                is RegistrationIntent.Register -> vm.register()
                is RegistrationIntent.SetDateOfBirth -> vm.updateUiState(dateOfBirth = intent.dateOfBirth)
                is RegistrationIntent.SetFirstName -> vm.updateUiState(firstName = intent.firstName)
                is RegistrationIntent.SetGender -> vm.updateUiState(gender = intent.gender)
                is RegistrationIntent.SetLastName -> vm.updateUiState(lastName = intent.lastName)
                is RegistrationIntent.OpenDateBottomSheet -> scope.launch {
                    vm.setDatePickerVisible(true)
                    focusManager.clearFocus(true)
                }
            }
        }
    )

    if (showErrorDialog) {
        BaseDialog(
            title = stringResource(R.string.error),
            description = currentErrorMessageId?.let { stringResource(it) },
            actionText = stringResource(R.string.ok),
            onAction = { vm.dismissErrorDialog() },
            onDismiss = { vm.dismissErrorDialog() }
        )
    }

    if (loading) {
        LoadingDialog()
    }
}