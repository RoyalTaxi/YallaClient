package uz.yalla.client.feature.domain.model


data class OrderHistoryModel(
    val comment: String,
    val date: String,
    val time: String,
    val executor: Executor,
    val id: Int,
    val number: Long,
    val paymentType: String,
    val service: String,
    val status: String,
    val taxi: Taxi,
    val track: List<Track>
) {
    data class Executor(
        val cords: Coordinates,
        val driver: Driver,
        val fatherName: String,
        val givenNames: String,
        val id: Int,
        val phone: String,
        val surName: String
    ) {
        data class Coordinates(
            val heading: Double,
            val lat: Double,
            val lng: Double
        )

        data class Driver(
            val callSign: String,
            val color: Color,
            val id: Int,
            val mark: String,
            val model: String,
            val stateNumber: String
        ) {
            data class Color(
                val color: String,
                val name: String
            )
        }
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

    data class Track(
        val lat: Double,
        val lng: Double,
        val speed: Double,
        val status: String,
        val time: Long
    )
}