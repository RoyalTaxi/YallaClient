package uz.ildam.technologies.yalla.feature.history.domain.model

sealed interface OrdersHistory {
    data class Item(
        val id: Int,
        val service: String,
        val status: String,
        val taxi: OrdersHistoryModel.Taxi,
        val date: String,
        val time: String
    ) : OrdersHistory

    data class Date(
        val date: String
    ) : OrdersHistory
}

