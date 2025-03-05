package uz.yalla.client.core.domain.model

data class Executor(
    val id: Int,
    val lat: Double,
    val lng: Double,
    val heading: Double,
    val distance: Double
)