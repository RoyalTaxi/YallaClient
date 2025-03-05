package uz.yalla.client.service.order.request.order

import kotlinx.serialization.Serializable

@Serializable
data class RateTheRideRequest(
    val order_id: Int,
    val ball: Int,
    val comment: String
)