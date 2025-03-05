package uz.yalla.client.service.order.request.order

import kotlinx.serialization.Serializable

@Serializable
data class CancelOrderReason(
    val reason_id: Int,
    val reason_comment: String
)