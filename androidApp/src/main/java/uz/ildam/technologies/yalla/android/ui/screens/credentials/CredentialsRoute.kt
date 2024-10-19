package uz.ildam.technologies.yalla.android.ui.screens.credentials

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalFocusManager
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.threeten.bp.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun CredentialsRoute(
    number: String,
    secretKey: String,
    onBack: () -> Unit,
    onNext: () -> Unit,
    vm: CredentialsViewModel = koinViewModel()
) {
    var firstName by rememberSaveable { mutableStateOf("") }
    var lastName by rememberSaveable { mutableStateOf("") }
    var dateOfBirth by rememberSaveable { mutableStateOf<LocalDate?>(null) }
    var gender by rememberSaveable { mutableStateOf("NOT_SELECTED") }
    val focusManager = LocalFocusManager.current
    val scope = rememberCoroutineScope()
    val state = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    )

    LaunchedEffect(Unit) {
        launch {
            vm.events.collect {
                when (it) {
                    is CredentialsEvent.Error -> {}
                    is CredentialsEvent.Loading -> {}
                    is CredentialsEvent.Success -> onNext()
                }
            }
        }
    }

    CredentialsScreen(
        firstName = firstName,
        lastName = lastName,
        dateOfBirth = dateOfBirth,
        gender = gender,
        state = state,
        onClickDate = {
            scope.launch { state.bottomSheetState.show() }
            focusManager.clearFocus(true)
        },
        onUpdateFirstName = { firstName = it },
        onUpdateLastName = { lastName = it },
        onSelectDate = { dateOfBirth = it },
        onUpdateGender = { gender = it },
        onDismissRequestBottomSheet = { scope.launch { state.bottomSheetState.hide() } },
        onBack = onBack,
        onRegister = {
            dateOfBirth?.let { dateOfBirth ->
                vm.register(
                    number = "998$number",
                    firstName = firstName,
                    lastName = lastName,
                    gender = gender,
                    dateOfBirth = dateOfBirth,
                    key = secretKey
                )
            }
        }
    )
}