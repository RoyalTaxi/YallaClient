package uz.ildam.technologies.yalla.feature.order.data.request.tariff

import kotlinx.serialization.Serializable

@Serializable
data class GetTimeOutRequest(
    val lng: Double,
    val lat: Double,
    val tariff_id: Int
)