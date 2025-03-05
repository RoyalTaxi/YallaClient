package uz.yalla.client.service.order.request.tariff

import kotlinx.serialization.Serializable


@Serializable
data class GetTariffsRequest(
    val option_ids: List<Int>,
    val coords: List<Coordination>,
    val address_id: Int,
) {
    @Serializable
    data class Coordination(
        val lat: Double,
        val lng: Double
    )
}