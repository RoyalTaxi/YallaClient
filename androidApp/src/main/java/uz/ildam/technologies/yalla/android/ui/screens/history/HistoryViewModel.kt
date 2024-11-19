package uz.ildam.technologies.yalla.android.ui.screens.history

import uz.ildam.technologies.yalla.feature.history.domain.usecase.GetOrderHistoryUseCase
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import uz.ildam.technologies.yalla.feature.history.domain.model.OrderHistory

class HistoryViewModel(
    private val getOrderHistoryUseCase: GetOrderHistoryUseCase
) : ViewModel() {
    private val _orders = MutableSharedFlow<PagingData<OrderHistory>>()
    val orders = _orders.asSharedFlow()

    init {
        getOrders()
    }

    private fun getOrders() = viewModelScope.launch {
        getOrderHistoryUseCase().collectLatest {
            _orders.emit(it)
        }
    }
}