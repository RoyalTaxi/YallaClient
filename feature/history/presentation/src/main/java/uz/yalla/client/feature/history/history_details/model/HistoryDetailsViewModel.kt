package uz.yalla.client.feature.history.history_details.model

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import uz.yalla.client.core.common.viewmodel.BaseViewModel
import uz.yalla.client.core.common.viewmodel.LifeCycleAware
import uz.yalla.client.feature.domain.usecase.GetOrderHistoryUseCase
import uz.yalla.client.feature.history.history_details.intent.HistoryDetailsSideEffect
import uz.yalla.client.feature.history.history_details.intent.HistoryDetailsState
import uz.yalla.client.feature.order.domain.usecase.tariff.GetTariffsUseCase

class HistoryDetailsViewModel(
    internal val orderId: Int,
    internal val getOrderHistoryUseCase: GetOrderHistoryUseCase,
    internal val getTariffsUseCase: GetTariffsUseCase
) : BaseViewModel(), LifeCycleAware, ContainerHost<HistoryDetailsState, HistoryDetailsSideEffect> {

    override val container: Container<HistoryDetailsState, HistoryDetailsSideEffect> =
        container(HistoryDetailsState.INITIAL)

    override var scope: CoroutineScope? = null

    override fun onCreate() {
        super.onCreate()
        viewModelScope.launch {
            intent {
                postSideEffect(HistoryDetailsSideEffect.UpdateRoute)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        scope = CoroutineScope(viewModelScope.coroutineContext + SupervisorJob())
        scope?.launch { getOrderHistory(orderId) }
    }
}