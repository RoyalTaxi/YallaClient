package uz.yalla.client.feature.domain.model

import uz.yalla.client.core.domain.model.OrderStatus

data class OrdersHistoryModel(
    val dateTime: Long,
    val id: Int,
    val service: String,
    val status: OrderStatus,
    val taxi: Taxi
) {
    data class Taxi(
        val routes: List<Route>,
        val totalPrice: String,
    ) {
        data class Route(
            val cords: Cords,
            val fullAddress: String,
            val index: Int
        ) {
            data class Cords(
                val lat: Double,
                val lng: Double
            )
        }
    }
}