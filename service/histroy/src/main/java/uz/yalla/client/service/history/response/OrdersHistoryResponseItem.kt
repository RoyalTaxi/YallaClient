package uz.yalla.client.service.history.response

import kotlinx.serialization.Serializable

@Serializable
data class OrdersHistoryResponseItem(
    val date_time: Long?,
    val id: Int?,
    val service: String?,
    val status: String?,
    val taxi: Taxi?
) {
    @Serializable
    data class Taxi(
        val routes: List<Route>,
        val total_price: Int?
    ) {
        @Serializable
        data class Route(
            val coords: Cords?,
            val full_address: String?,
            val index: Int?
        ) {
            @Serializable
            data class Cords(
                val lat: Double?,
                val lng: Double?
            )
        }
    }
}