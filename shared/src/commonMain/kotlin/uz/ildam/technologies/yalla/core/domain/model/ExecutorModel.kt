package uz.ildam.technologies.yalla.core.domain.model

data class ExecutorModel(
    val id: Long,
    val lat: Double,
    val lng: Double,
    val heading: Double,
    val distance: Double
)