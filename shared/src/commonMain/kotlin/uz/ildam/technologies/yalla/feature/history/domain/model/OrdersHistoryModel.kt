package uz.ildam.technologies.yalla.feature.history.domain.model

data class OrdersHistoryModel(
    val dateTime: Long,
    val id: Int,
    val service: String,
    val status: String,
    val taxi: Taxi
) {
    data class Taxi(
        val bonusAmount: Int,
        val clientTotalPrice: Int,
        val distance: Double,
        val fixedPrice: Boolean,
        val routes: List<Route>,
        val startPrice: Int,
        val tariff: String,
        val tariffCategoryId: Int,
        val totalPrice: String,
        val useTheBonus: Boolean
    ) {
        data class Route(
            val cords: Cords,
            val fullAddress: String,
            val index: Int
        ) {
            data class Cords(
                val lat: Double,
                val lng: Double
            )
        }
    }
}