package uz.ildam.technologies.yalla.android.ui.screens.history

import uz.ildam.technologies.yalla.feature.history.domain.usecase.GetOrdersHistoryUseCase
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import uz.ildam.technologies.yalla.feature.history.domain.model.OrdersHistory

class HistoryViewModel(
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