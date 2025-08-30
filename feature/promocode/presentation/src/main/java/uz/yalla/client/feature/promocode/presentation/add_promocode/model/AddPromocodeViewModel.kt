package uz.yalla.client.feature.promocode.presentation.add_promocode.model

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import uz.yalla.client.core.common.viewmodel.BaseViewModel
import uz.yalla.client.core.common.viewmodel.LifeCycleAware
import uz.yalla.client.feature.promocode.domain.usecase.ActivatePromocodeUseCase
import uz.yalla.client.feature.promocode.presentation.add_promocode.intent.AddPromocodeSideEffect
import uz.yalla.client.feature.promocode.presentation.add_promocode.intent.AddPromocodeState

class AddPromocodeViewModel(
    internal val activatePromocodeUseCase: ActivatePromocodeUseCase
) : BaseViewModel(), LifeCycleAware, ContainerHost<AddPromocodeState, AddPromocodeSideEffect> {

    override val container: Container<AddPromocodeState, AddPromocodeSideEffect> =
        container(AddPromocodeState.INITIAL)

    override var scope: CoroutineScope? = null

    override fun onCreate() {
        super.onCreate()
        viewModelScope.launch { }
    }

    override fun onStart() {
        super.onStart()
        scope = CoroutineScope(viewModelScope.coroutineContext + SupervisorJob())
        scope?.launch {
            intent {
                postSideEffect(AddPromocodeSideEffect.RequestFocus)
            }
        }
    }
}