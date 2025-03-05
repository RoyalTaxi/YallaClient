package uz.yalla.client.service.order.response.order

import kotlinx.serialization.Serializable

@Serializable
data class SettingResponse(
    val find_radius: Float?,
    val order_cancel_time: Int?,
    val reasons: List<CancelReason>?
) {
    @Serializable
    data class CancelReason(
        val id: Int?,
        val name: String?
    )
}