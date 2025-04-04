package uz.yalla.client.service.history.response

import kotlinx.serialization.Serializable
import uz.yalla.client.core.service.model.ServiceRemoteModel

@Serializable
data class OrderHistoryResponse(
    val date_time: Long?,
    val executor: Executor?,
    val status: String?,
    val taxi: Taxi?,
) {
    @Serializable
    data class Executor(
        val driver: Driver?
    ) {
        @Serializable
        data class Driver(
            val mark: String?,
            val model: String?,
            val state_number: String?
        )
    }

    @Serializable
    data class Taxi(
        val bonus_amount: Int?,
        val client_total_price: Int?,
        val distance: Double?,
        val fixed_price: Boolean?,
        val routes: List<Route>?,
        val routes_for_robot: List<Route>?,
        val services: List<ServiceRemoteModel>?,
        val start_price: Int?,
        val tariff: String?,
        val total_price: Int?,
        val use_the_bonus: Boolean?
    ) {
        @Serializable
        data class Route(
            val coords: Coordinates?,
            val full_address: String?,
            val index: Int?
        ) {
            @Serializable
            data class Coordinates(
                val lat: Double?,
                val lng: Double?
            )
        }
    }
}