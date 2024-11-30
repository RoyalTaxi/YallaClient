package uz.ildam.technologies.yalla.feature.order.data.response.order

import kotlinx.serialization.Serializable

@Serializable
data class OrderTaxiResponse(
    val order_id: Int?
)