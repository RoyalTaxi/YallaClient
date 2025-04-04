package uz.yalla.client.feature.domain.model

import uz.yalla.client.core.domain.model.OrderStatus

sealed interface OrdersHistory {
    data class Item(
        val id: Int,
        val service: String,
        val status: OrderStatus,
        val taxi: OrdersHistoryModel.Taxi,
        val date: String,
        val time: String
    ) : OrdersHistory

    data class Date(
        val date: String
    ) : OrdersHistory
}

