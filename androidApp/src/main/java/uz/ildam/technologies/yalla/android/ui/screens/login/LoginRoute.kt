package uz.ildam.technologies.yalla.android.ui.screens.login

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalFocusManager
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun LoginRoute(
    onBack: () -> Unit,
    onNext: (String, Int) -> Unit,
    vm: LoginViewModel = koinViewModel()
) {
    var buttonState by rememberSaveable { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    var number by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(Unit) {
        launch {
            vm.events.collect { event ->
                when (event) {
                    is LoginEvent.Error -> buttonState = false
                    is LoginEvent.Loading -> buttonState = false
                    is LoginEvent.Success -> onNext(number, event.data.time)
                }
            }
        }
    }

    LoginScreen(
        number = number,
        buttonState = buttonState,
        focusManager = focusManager,
        onBack = onBack,
        onSend = { vm.sendAuthCode("998$number") },
        onUpdateNumber = {
            number = it
            buttonState = it.length == 9
        }
    )
}