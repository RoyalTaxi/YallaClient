package uz.yalla.client.service.order.response.order

import kotlinx.serialization.Serializable

@Serializable
data class OrderTaxiResponse(
    val order_id: Int?
)