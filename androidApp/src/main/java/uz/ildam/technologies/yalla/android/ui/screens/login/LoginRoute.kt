package uz.ildam.technologies.yalla.android.ui.screens.login

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalFocusManager
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun LoginRoute(
    onBack: () -> Unit,
    onNext: (String, Int) -> Unit,
    vm: LoginViewModel = koinViewModel()
) {
    val focusManager = LocalFocusManager.current
    val uiState by vm.uiState.collectAsState()

    LaunchedEffect(Unit) {
        launch {
            vm.eventFlow.collectLatest { actionState ->
                when (actionState) {
                    is LoginActionState.Error -> vm.updateUiState(buttonState = false)
                    is LoginActionState.Loading -> vm.updateUiState(buttonState = false)
                    is LoginActionState.Success -> onNext(uiState.number, actionState.data.time)
                }
            }
        }
    }

    LoginScreen(
        uiState = uiState,
        onIntent = { intent ->
            when (intent) {
                is LoginIntent.ClearFocus -> focusManager.clearFocus(true)
                is LoginIntent.NavigateBack -> onBack()
                is LoginIntent.SendCode -> vm.sendAuthCode()
                is LoginIntent.SetNumber -> vm.updateUiState(number = intent.number)
            }
        }
    )
}