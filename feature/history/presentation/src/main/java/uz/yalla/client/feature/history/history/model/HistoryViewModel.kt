package uz.yalla.client.feature.history.history.model

import androidx.paging.PagingData
import app.cash.paging.cachedIn
import kotlinx.coroutines.flow.Flow
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import uz.yalla.client.core.common.viewmodel.BaseViewModel
import uz.yalla.client.feature.domain.model.OrdersHistory
import uz.yalla.client.feature.domain.usecase.GetOrdersHistoryUseCase
import uz.yalla.client.feature.history.history.intent.HistorySideEffect

class HistoryViewModel(
    getOrdersHistoryUseCase: GetOrdersHistoryUseCase
) : BaseViewModel(), ContainerHost<Unit, HistorySideEffect> {

    override val container: Container<Unit, HistorySideEffect> =
        container(Unit)

    val orders: Flow<PagingData<OrdersHistory>> = getOrdersHistoryUseCase()
        .cachedIn(viewModelScope)
}