package uz.yalla.client.service.order.response.order

import kotlinx.serialization.Serializable
import uz.yalla.client.core.service.model.ServiceRemoteModel

@Serializable
data class ShowOrderResponse(
    val comment: String?,
    val date_time: Long?,
    val executor: ExecutorData?,
    val id: Int?,
    val payment_type: String?,
    val service: String?,
    val status: String?,
    val status_time: List<StatusTimeData>?,
    val taxi: TaxiData?,
) {
    @Serializable
    data class ExecutorData(
        val coords: CoordsData?,
        val driver: DriverData?,
        val father_name: String?,
        val given_names: String?,
        val id: Int?,
        val phone: String?,
        val sur_name: String?
    ) {
        @Serializable
        data class CoordsData(
            val heading: Double?,
            val lat: Double?,
            val lng: Double?
        )

        @Serializable
        data class DriverData(
            val callsign: String?,
            val color: ColorData?,
            val id: Int?,
            val mark: String?,
            val model: String?,
            val state_number: String?
        ) {
            @Serializable
            data class ColorData(
                val color: String?,
                val name: String?
            )
        }
    }

    @Serializable
    data class StatusTimeData(
        val status: String?,
        val time: Long?
    )

    @Serializable
    data class TaxiData(
        val bonus_amount: Int?,
        val client_total_price: Double?,
        val distance: Double?,
        val fixed_price: Boolean,
        val routes: List<RouteData>?,
        val services: List<ServiceRemoteModel>?,
        val start_price: Int?,
        val tariff: String?,
        val tariff_id: Int?,
        val total_price: Int?
    ) {
        @Serializable
        data class RouteData(
            val coords: CoordsData?,
            val full_address: String?,
            val index: Int?
        ) {
            @Serializable
            data class CoordsData(
                val lat: Double?,
                val lng: Double?
            )
        }
    }
}
