package uz.ildam.technologies.yalla.android.ui.screens.login

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import kotlinx.coroutines.launch
import uz.ildam.technologies.yalla.android.R
import uz.ildam.technologies.yalla.android.components.button.YallaButton
import uz.ildam.technologies.yalla.android.components.textfield.PhoneNumberTextField
import uz.ildam.technologies.yalla.android.components.toolbar.YallaToolbar
import uz.ildam.technologies.yalla.android.design.theme.YallaTheme
import uz.ildam.technologies.yalla.android.ui.screens.verification.VerificationScreen

class LoginScreen : Screen {

    @Composable
    override fun Content() {
        val screenModel = koinScreenModel<LoginModel>()
        val navigator = LocalNavigator.currentOrThrow
        val focusManager = LocalFocusManager.current

        var number by rememberSaveable { mutableStateOf("") }
        var buttonState by rememberSaveable { mutableStateOf(false) }

        LaunchedEffect(Unit) {
            launch {
                screenModel.events.collect { event ->
                    when (event) {
                        is LoginEvent.Error -> buttonState = false
                        is LoginEvent.Loading -> buttonState = false
                        is LoginEvent.Success -> {
                            navigator push VerificationScreen(number, event.data.time)
                        }
                    }
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(YallaTheme.color.white)
                .clickable(
                    onClick = { focusManager.clearFocus(true) },
                    role = Role.Image,
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                )
                .systemBarsPadding()
                .imePadding(),
        ) {
            YallaToolbar(onClick = navigator::pop)

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(20.dp)

            ) {
                Spacer(modifier = Modifier.height(40.dp))

                Text(
                    text = stringResource(id = R.string.enter_phone_number),
                    color = YallaTheme.color.black,
                    style = YallaTheme.font.headline
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = stringResource(id = R.string.we_send_code),
                    color = YallaTheme.color.gray,
                    style = YallaTheme.font.body
                )

                Spacer(modifier = Modifier.height(20.dp))

                PhoneNumberTextField(
                    number = number,
                    onUpdateNumber = {
                        number = it
                        buttonState = it.length == 9
                    }
                )

                Spacer(modifier = Modifier.weight(1f))
                Spacer(modifier = Modifier.height(32.dp))

                YallaButton(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(id = R.string.next),
                    enabled = buttonState,
                    onClick = { screenModel.sendAuthCode("998$number") }
                )
            }
        }
    }
}