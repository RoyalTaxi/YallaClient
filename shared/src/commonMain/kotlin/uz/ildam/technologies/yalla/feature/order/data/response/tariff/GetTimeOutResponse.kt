package uz.ildam.technologies.yalla.feature.order.data.response.tariff

import kotlinx.serialization.Serializable

@Serializable
data class GetTimeOutResponse(
    val executors: List<Executor>?,
    val timeout: Int?,
) {
    @Serializable
    data class Executor(
        val id: Int?,
        val lat: Double?,
        val lng: Double?,
        val heading: Double?,
        val distance: Double?
    )
}