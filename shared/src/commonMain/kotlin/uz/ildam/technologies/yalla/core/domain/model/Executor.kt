package uz.ildam.technologies.yalla.core.domain.model

data class Executor(
    val id: Long,
    val lat: Double,
    val lng: Double,
    val heading: Double,
    val distance: Double
)