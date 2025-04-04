package uz.yalla.client.feature.order.domain.model.response.order

import uz.yalla.client.core.domain.model.Executor
import uz.yalla.client.core.domain.model.OrderStatus
import uz.yalla.client.core.domain.model.PaymentType
import uz.yalla.client.core.domain.model.ServiceModel


data class ShowOrderModel(
    val comment: String,
    val dateTime: String,
    val executor: Executor,
    val id: Int,
    val paymentType: PaymentType,
    val service: String,
    val status: OrderStatus,
    val statusTime: List<StatusTime>,
    val taxi: Taxi,
) {
    data class Executor(
        val coords: Coords,
        val driver: Driver,
        val fatherName: String,
        val givenNames: String,
        val id: Int,
        val phone: String,
        val surName: String
    ) {
        data class Coords(
            val heading: Double,
            val lat: Double,
            val lng: Double
        )

        data class Driver(
            val callsign: String,
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

    data class StatusTime(
        val status: String,
        val time: Long
    )

    data class Taxi(
        val clientTotalPrice: Double,
        val distance: Double,
        val fixedPrice: Boolean,
        val routes: List<Route>,
        val services: List<ServiceModel>,
        val startPrice: Int,
        val tariff: String,
        val tariffId: Int,
        val totalPrice: Int
    ) {
        data class Route(
            val coords: Coords,
            val fullAddress: String,
            val index: Int
        ) {
            data class Coords(
                val lat: Double,
                val lng: Double
            )
        }
    }
}

fun ShowOrderModel.Executor.toCommonExecutor() = Executor(
    id = this.id,
    lat = this.coords.lat,
    lng = this.coords.lng,
    heading = this.coords.heading,
    distance = 0.0
)