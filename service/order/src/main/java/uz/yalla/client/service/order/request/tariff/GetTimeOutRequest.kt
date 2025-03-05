package uz.yalla.client.service.order.request.tariff

import kotlinx.serialization.Serializable

@Serializable
data class GetTimeOutRequest(
    val lng: Double,
    val lat: Double,
    val tariff_id: Int
)