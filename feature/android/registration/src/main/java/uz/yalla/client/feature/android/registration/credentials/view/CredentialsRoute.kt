package uz.yalla.client.feature.android.registration.credentials.view

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalFocusManager
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import uz.yalla.client.feature.android.registration.credentials.model.CredentialsActionState
import uz.yalla.client.feature.android.registration.credentials.model.CredentialsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun CredentialsRoute(
    number: String,
    secretKey: String,
    onBack: () -> Unit,
    onNext: () -> Unit,
    vm: CredentialsViewModel = koinViewModel()
) {
    val uiState by vm.uiState.collectAsState()
    val focusManager = LocalFocusManager.current
    val scope = rememberCoroutineScope()
    val sheetState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    )

    LaunchedEffect(Unit) {
        launch {
            vm.updateUiState(
                number = number,
                secretKey = secretKey
            )
        }

        launch {
            vm.actionFlow.collectLatest {
                when (it) {
                    is CredentialsActionState.Error -> {}
                    is CredentialsActionState.Loading -> {}
                    is CredentialsActionState.Success -> onNext()
                }
            }
        }
    }

    CredentialsScreen(
        uiState = uiState,
        sheetState = sheetState,
        onIntent = { intent ->
            when (intent) {
                is CredentialsIntent.CloseDateBottomSheet -> scope.launch { sheetState.bottomSheetState.hide() }
                is CredentialsIntent.NavigateBack -> onBack()
                is CredentialsIntent.Register -> vm.register()
                is CredentialsIntent.SetDateOfBirth -> vm.updateUiState(dateOfBirth = intent.dateOfBirth)
                is CredentialsIntent.SetFirstName -> vm.updateUiState(firstName = intent.firstName)
                is CredentialsIntent.SetGender -> vm.updateUiState(gender = intent.gender)
                is CredentialsIntent.SetLastName -> vm.updateUiState(lastName = intent.lastName)
                is CredentialsIntent.OpenDateBottomSheet -> scope.launch {
                    sheetState.bottomSheetState.show()
                    focusManager.clearFocus(true)
                }
            }
        }
    )
}