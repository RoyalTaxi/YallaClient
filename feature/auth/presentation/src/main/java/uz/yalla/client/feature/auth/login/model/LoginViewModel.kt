package uz.yalla.client.feature.auth.login.model

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import uz.yalla.client.core.common.viewmodel.BaseViewModel
import uz.yalla.client.core.common.viewmodel.LifeCycleAware
import uz.yalla.client.core.domain.local.StaticPreferences
import uz.yalla.client.feature.auth.domain.usecase.auth.SendCodeUseCase
import uz.yalla.client.feature.auth.login.intent.LoginIntent
import uz.yalla.client.feature.auth.login.intent.LoginSideEffect
import uz.yalla.client.feature.auth.login.intent.LoginState

class LoginViewModel(
    staticPreferences: StaticPreferences,
    private val sendCodeUseCase: SendCodeUseCase
) : BaseViewModel(), LifeCycleAware, ContainerHost<LoginState, LoginSideEffect> {

    override val container: Container<LoginState, LoginSideEffect> = container(LoginState.INITIAL)
    override val scope: CoroutineScope = viewModelScope + SupervisorJob()

    init {
        staticPreferences.skipOnboarding = true
    }

    override fun onAppear() {
        scope.launch {
            container.stateFlow.distinctUntilChangedBy { it.phoneNumber }.collect {
                intent { reduce { state.copy(sendButtonState = it.isPhoneNumberValid()) } }
            }
        }
    }

    fun onIntent(intent: LoginIntent) = intent {
        when (intent) {
            is LoginIntent.SendCode -> sendAuthCode(hash = intent.hash)
            is LoginIntent.UpdatePhoneNumber -> reduce { state.copy(phoneNumber = intent.number) }
        }
    }

    fun sendAuthCode(hash: String?) = viewModelScope.launchWithLoading {
        intent {
            sendCodeUseCase(number = state.phoneNumber, hash = hash).onSuccess { result ->
                postSideEffect(
                    LoginSideEffect.NavigateToVerification(
                        phoneNumber = state.phoneNumber,
                        seconds = result.time
                    )
                )
            }.onFailure(::handleException)
        }
    }

    override fun onDisappear() {
        scope.cancel()
    }
}
