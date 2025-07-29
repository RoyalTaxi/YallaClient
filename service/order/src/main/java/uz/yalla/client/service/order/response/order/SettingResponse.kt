package uz.yalla.client.service.order.response.order

import kotlinx.serialization.Serializable

@Serializable
data class SettingResponse(
    val find_radius: Float?,
    val order_cancel_time: Int?,
    val reasons: List<CancelReason>?,
    val min_bonus: Long?,
    val max_bonus: Long?,
    val use_the_bonus: Boolean?,
) {
    @Serializable
    data class CancelReason(
        val id: Int?,
        val name: String?
    )
}
