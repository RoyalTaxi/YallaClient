package uz.ildam.technologies.yalla.feature.history.domain.model

sealed interface OrderHistory {
    data class Item(
        val id: Int,
        val service: String,
        val status: String,
        val taxi: OrderHistoryModel.Taxi,
        val date: String,
        val time: String
    ) : OrderHistory

    data class Date(
        val date: String
    ) : OrderHistory
}

