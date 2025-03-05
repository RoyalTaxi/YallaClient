package uz.yalla.client.service.history.response

import kotlinx.serialization.Serializable

@Serializable
data class OrderHistoryResponse(
    val comment: String?,
    val date_time: Long?,
    val executor: Executor?,
    val id: Int?,
    val number: Long?,
    val payment_type: String?,
    val service: String?,
    val status: String?,
    val taxi: Taxi?,
    val track: List<Track>?
) {
    @Serializable
    data class Executor(
        val coords: Coordinates?,
        val driver: Driver?,
        val father_name: String?,
        val given_names: String?,
        val id: Int?,
        val phone: String?,
        val sur_name: String?
    ) {
        @Serializable
        data class Coordinates(
            val heading: Double?,
            val lat: Double?,
            val lng: Double?
        )

        @Serializable
        data class Driver(
            val callsign: String?,
            val color: Color?,
            val id: Int?,
            val mark: String?,
            val model: String?,
            val state_number: String?
        ) {
            @Serializable
            data class Color(
                val color: String?,
                val name: String?
            )
        }
    }

    @Serializable
    data class Taxi(
        val bonus_amount: Int?,
        val client_total_price: Int?,
        val distance: Double?,
        val fixed_price: Boolean?,
        val routes: List<Route>?,
        val routes_for_robot: List<Route>?,
        val services: String?,
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

    @Serializable
    data class Track(
        val lat: Double?,
        val lng: Double?,
        val speed: Double?,
        val status: String?,
        val time: Long?
    )
}