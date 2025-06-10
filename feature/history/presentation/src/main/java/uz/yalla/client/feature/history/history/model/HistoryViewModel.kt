package uz.yalla.client.feature.history.history.model

import androidx.paging.PagingData
import app.cash.paging.cachedIn
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import uz.yalla.client.core.common.viewmodel.BaseViewModel
import uz.yalla.client.feature.domain.model.OrdersHistory
import uz.yalla.client.feature.domain.usecase.GetOrdersHistoryUseCase

internal class HistoryViewModel(
    private val getOrdersHistoryUseCase: GetOrdersHistoryUseCase
) : BaseViewModel() {

    private val _orders = MutableStateFlow<PagingData<OrdersHistory>>(PagingData.empty())
    val orders = _orders.asStateFlow()

    init {
        getOrders()
    }

    fun getOrders() = viewModelScope.launch {
        getOrdersHistoryUseCase()
            .cachedIn(viewModelScope)
            .collectLatest {
                _orders.emit(it)
            }
    }
}