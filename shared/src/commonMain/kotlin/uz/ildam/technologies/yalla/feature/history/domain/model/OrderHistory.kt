package uz.ildam.technologies.yalla.feature.history.domain.model

sealed interface OrderHistory

data class OrderHistoryItem(
    val id: Int,
    val service: String,
    val status: String,
    val taxi: OrderHistoryModel.Taxi,
    val date: String,
    val time: String
) : OrderHistory

data class OrderHistoryDate(
    val date: String
) : OrderHistory