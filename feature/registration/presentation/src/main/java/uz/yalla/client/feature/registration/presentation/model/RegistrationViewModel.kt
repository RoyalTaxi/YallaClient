package uz.yalla.client.feature.registration.presentation.model

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import uz.yalla.client.core.common.viewmodel.BaseViewModel
import uz.yalla.client.core.common.viewmodel.LifeCycleAware
import uz.yalla.client.core.domain.local.AppPreferences
import uz.yalla.client.core.domain.local.StaticPreferences
import uz.yalla.client.feature.auth.domain.usecase.register.RegisterUseCase
import uz.yalla.client.feature.registration.presentation.intent.RegistrationSideEffect
import uz.yalla.client.feature.registration.presentation.intent.RegistrationState

internal class RegistrationViewModel(
    internal val secretKey: String,
    internal val phoneNumber: String,
    internal val registerUseCase: RegisterUseCase,
    internal val appPreferences: AppPreferences,
    internal val staticPreferences: StaticPreferences
) : BaseViewModel(), LifeCycleAware, ContainerHost<RegistrationState, RegistrationSideEffect> {

    override val container: Container<RegistrationState, RegistrationSideEffect> =
        container(RegistrationState.INITIAL)

    override var scope: CoroutineScope? = null

    override fun onCreate() {
        super.onCreate()
        viewModelScope.launch {
            intent {
                reduce {
                    state.copy(
                        secretKey = secretKey,
                        phoneNumber = phoneNumber
                    )
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        scope = CoroutineScope(viewModelScope.coroutineContext + SupervisorJob())
    }
}