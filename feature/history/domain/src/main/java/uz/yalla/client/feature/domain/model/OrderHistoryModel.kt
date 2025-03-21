package uz.yalla.client.feature.domain.model


data class OrderHistoryModel(
    val date: String,
    val time: String,
    val executor: Executor,
    val status: String,
    val taxi: Taxi,
) {
    data class Executor(
        val driver: Driver
    ) {
        data class Driver(
            val mark: String,
            val model: String,
            val stateNumber: String
        )
    }

    data class Taxi(
        val bonusAmount: Int,
        val clientTotalPrice: Int,
        val distance: Double,
        val fixedPrice: Boolean,
        val routes: List<Route>,
        val routesForRobot: List<Route>,
        val services: String,
        val startPrice: Int,
        val tariff: String,
        val totalPrice: Int,
        val useTheBonus: Boolean
    ) {
        data class Route(
            val cords: Coordinates,
            val fullAddress: String,
            val index: Int
        ) {
            data class Coordinates(
                val lat: Double,
                val lng: Double
            )
        }
    }
}