package uz.yalla.client.feature.order.presentation.order_canceled.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import uz.yalla.client.feature.order.presentation.order_canceled.view.OrderCanceledSheetChannel
import uz.yalla.client.feature.order.presentation.order_canceled.view.OrderCanceledSheetIntent

class OrderCanceledSheetViewModel : ViewModel() {

    fun onIntent(intent: OrderCanceledSheetIntent) {
        viewModelScope.launch {
            OrderCanceledSheetChannel.sendIntent(intent)
        }
    }
}