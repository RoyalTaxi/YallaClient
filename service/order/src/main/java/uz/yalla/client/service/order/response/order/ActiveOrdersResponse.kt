package uz.yalla.client.service.order.response.order

import kotlinx.serialization.Serializable

@Serializable
data class ActiveOrdersResponse(
    val list: List<ShowOrderResponse>?
)