package uz.yalla.client.feature.places.places.model

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import uz.yalla.client.core.common.viewmodel.BaseViewModel
import uz.yalla.client.core.common.viewmodel.LifeCycleAware
import uz.yalla.client.feature.order.domain.usecase.FindAllPlacesUseCase
import uz.yalla.client.feature.places.places.intent.AddressesSideEffect
import uz.yalla.client.feature.places.places.intent.AddressesState

internal class AddressesViewModel(
    internal val findAllPlacesUseCase: FindAllPlacesUseCase
) : BaseViewModel(), LifeCycleAware, ContainerHost<AddressesState, AddressesSideEffect> {

    override val container: Container<AddressesState, AddressesSideEffect> =
        container(AddressesState.INITIAL)

    override var scope: CoroutineScope? = null

    override fun onCreate() {
        super.onCreate()
        viewModelScope.launch { }
    }

    override fun onStart() {
        super.onStart()
        scope = CoroutineScope(viewModelScope.coroutineContext + SupervisorJob())
        scope?.launch {
            findAllAddresses()
        }
    }
}