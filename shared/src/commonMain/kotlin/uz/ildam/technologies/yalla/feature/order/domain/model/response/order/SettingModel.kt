package uz.ildam.technologies.yalla.feature.order.domain.model.response.order

data class SettingModel(
    val findRadius: Float,
    val orderCancelTime: Int,
    val reasons: List<CancelReason>
) {
    data class CancelReason(
        val id: Int,
        val name: String
    )
}