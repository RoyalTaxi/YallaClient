package uz.yalla.client.feature.order.domain.model.response.order

data class SettingModel(
    val findRadius: Float,
    val orderCancelTime: Int,
    val reasons: List<CancelReason>,
    val minBonus: Int,
    val maxBonus: Int,
    val isBonusEnabled: Boolean,
) {
    data class CancelReason(
        val id: Int,
        val name: String
    )
}