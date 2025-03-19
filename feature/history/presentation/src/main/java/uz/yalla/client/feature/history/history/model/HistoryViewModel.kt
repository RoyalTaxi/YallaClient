package uz.yalla.client.feature.history.history.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import uz.yalla.client.feature.domain.model.OrdersHistory
import uz.yalla.client.feature.domain.usecase.GetOrdersHistoryUseCase

internal class HistoryViewModel(
    private val getOrdersHistoryUseCase: GetOrdersHistoryUseCase
) : ViewModel() {
    private val _orders = MutableSharedFlow<PagingData<OrdersHistory>>()
    val orders = _orders.asSharedFlow()

    init {
        getOrders()
    }

    fun getOrders() = viewModelScope.launch {
        getOrdersHistoryUseCase().collectLatest {
            _orders.emit(it)
        }
    }
}