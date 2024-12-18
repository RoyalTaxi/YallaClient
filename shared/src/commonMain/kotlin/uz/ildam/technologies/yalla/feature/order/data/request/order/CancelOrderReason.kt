package uz.ildam.technologies.yalla.feature.order.data.request.order

import kotlinx.serialization.Serializable

@Serializable
data class CancelOrderReason(
    val reason_id: Int,
    val reason_comment: String
)