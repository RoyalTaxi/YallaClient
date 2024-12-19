package uz.ildam.technologies.yalla.feature.order.domain.model.response.order

sealed class OrderStatus(val value: String) {
    data object New : OrderStatus("new")
    data object Sending : OrderStatus("sending")
    data object UserSending : OrderStatus("user_sending")
    data object NonStopSending : OrderStatus("non_stop_sending")
    data object AtAddress : OrderStatus("at_address")
    data object InFetters : OrderStatus("in_fetters")
    data object Appointed : OrderStatus("appointed")
    data object Completed : OrderStatus("completed")
    data object Canceled : OrderStatus("canceled")

    companion object {
        fun from(value: String?): OrderStatus {
            return when (value) {
                New.value -> New
                Sending.value -> Sending
                UserSending.value -> UserSending
                NonStopSending.value -> NonStopSending
                AtAddress.value -> AtAddress
                InFetters.value -> InFetters
                Appointed.value -> Appointed
                Completed.value -> Completed
                Canceled.value -> Canceled
                else -> throw IllegalArgumentException("Unknown OrderStatus: $value")
            }
        }
    }
}