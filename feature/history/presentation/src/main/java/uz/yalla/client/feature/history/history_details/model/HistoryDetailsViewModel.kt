package uz.yalla.client.feature.history.history_details.model

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import uz.yalla.client.core.common.map.static.model.StaticMapViewModel
import uz.yalla.client.core.common.viewmodel.BaseViewModel
import uz.yalla.client.core.common.viewmodel.LifeCycleAware
import uz.yalla.client.feature.domain.usecase.GetOrderHistoryUseCase
import uz.yalla.client.feature.history.history_details.intent.HistoryDetailsSideEffect
import uz.yalla.client.feature.history.history_details.intent.HistoryDetailsState
import uz.yalla.client.feature.home.domain.usecase.GetRoutingUseCase

class HistoryDetailsViewModel(
    internal val orderId: Int,
    internal val staticMapViewModel: StaticMapViewModel,
    internal val getOrderHistoryUseCase: GetOrderHistoryUseCase,
    internal val getRoutingUseCase: GetRoutingUseCase,
) : BaseViewModel(), LifeCycleAware, ContainerHost<HistoryDetailsState, HistoryDetailsSideEffect> {

    override val container: Container<HistoryDetailsState, HistoryDetailsSideEffect> =
        container(HistoryDetailsState.INITIAL)

    override var scope: CoroutineScope? = null

    override fun onStart() {
        super.onStart()
        scope = CoroutineScope(viewModelScope.coroutineContext + SupervisorJob())
        scope?.launch { getOrderHistory(orderId) }
        scope?.launch { observeRoute() }
        scope?.launch { observeLocations() }
    }
}