package uz.yalla.client.feature.android.auth.login.view

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalFocusManager
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import uz.yalla.client.feature.android.auth.login.model.LoginActionState
import uz.yalla.client.feature.android.auth.login.model.LoginViewModel
import uz.yalla.client.feature.core.dialogs.LoadingDialog

@Composable
internal fun LoginRoute(
    onBack: () -> Unit,
    onNext: (String, Int) -> Unit,
    vm: LoginViewModel = koinViewModel()
) {
    val focusManager = LocalFocusManager.current
    val uiState by vm.uiState.collectAsState()
    var loading by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        launch {
            vm.actionFlow.collectLatest { actionState ->
                when (actionState) {
                    is LoginActionState.Error -> {
                        loading = false
                        vm.setButtonState(true)
                    }

                    is LoginActionState.Loading -> {
                        loading = true
                        vm.setButtonState(false)
                    }

                    is LoginActionState.Success -> {
                        loading = false
                        vm.setButtonState(true)
                        onNext(uiState.number, actionState.data.time)
                    }
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
                is LoginIntent.SetNumber -> vm.setNumber(number = intent.number)
            }
        }
    )

    if (loading) LoadingDialog()
}