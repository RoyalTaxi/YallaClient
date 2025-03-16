package uz.yalla.client.feature.registration.presentation.view

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
import uz.yalla.client.feature.registration.presentation.model.RegistrationActionState
import uz.yalla.client.feature.registration.presentation.model.RegistrationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun RegistrationRoute(
    number: String,
    secretKey: String,
    onBack: () -> Unit,
    onNext: () -> Unit,
    vm: RegistrationViewModel = koinViewModel()
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
                    is RegistrationActionState.Error -> {}
                    is RegistrationActionState.Loading -> {}
                    is RegistrationActionState.Success -> onNext()
                }
            }
        }
    }

    RegistrationScreen(
        uiState = uiState,
        sheetState = sheetState,
        onIntent = { intent ->
            when (intent) {
                is RegistrationIntent.CloseDateBottomSheet -> scope.launch { sheetState.bottomSheetState.hide() }
                is RegistrationIntent.NavigateBack -> onBack()
                is RegistrationIntent.Register -> vm.register()
                is RegistrationIntent.SetDateOfBirth -> vm.updateUiState(dateOfBirth = intent.dateOfBirth)
                is RegistrationIntent.SetFirstName -> vm.updateUiState(firstName = intent.firstName)
                is RegistrationIntent.SetGender -> vm.updateUiState(gender = intent.gender)
                is RegistrationIntent.SetLastName -> vm.updateUiState(lastName = intent.lastName)
                is RegistrationIntent.OpenDateBottomSheet -> scope.launch {
                    sheetState.bottomSheetState.show()
                    focusManager.clearFocus(true)
                }
            }
        }
    )
}