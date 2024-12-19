package uz.ildam.technologies.yalla.feature.history.data.response

import kotlinx.serialization.Serializable

@Serializable
data class OrdersHistoryResponseItem(
    val date_time: Long?,
    val id: Long?,
    val service: String?,
    val status: String?,
    val taxi: Taxi?
) {
    @Serializable
    data class Taxi(
        val bonus_amount: Int?,
        val client_total_price: Int?,
        val distance: Double?,
        val fixed_price: Boolean?,
        val routes: List<Route>,
        val start_price: Int?,
        val tariff: String?,
        val tariff_category_id: Int?,
        val total_price: Int?,
        val use_the_bonus: Boolean?
    ) {
        @Serializable
        data class Route(
            val coords: Cords?,
            val full_address: String?,
            val index: Int?
        ) {
            @Serializable
            data class Cords(
                val lat: Double?,
                val lng: Double?
            )
        }
    }
}