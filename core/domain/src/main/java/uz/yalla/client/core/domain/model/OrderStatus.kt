package uz.yalla.client.core.domain.model

sealed class OrderStatus(val value: String) {
    data object New : OrderStatus("new")
    data object Sending : OrderStatus("sending")
    data object UserSending : OrderStatus("user_sending")
    data object NonStopSending : OrderStatus("nonstop_sending")
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
                else -> Canceled
            }
        }

        val nonInteractive = setOfNotNull(New, Sending, UserSending, NonStopSending)

        val cancellable = nonInteractive + setOfNotNull(AtAddress)
    }
}