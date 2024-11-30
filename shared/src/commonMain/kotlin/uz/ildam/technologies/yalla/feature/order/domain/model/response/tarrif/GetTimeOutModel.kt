package uz.ildam.technologies.yalla.feature.order.domain.model.response.tarrif

data class GetTimeOutModel(
    val executors: List<Executor>,
    val timeout: Int,
) {
    data class Executor(
        val id: Int,
        val lat: Double,
        val lng: Double,
        val heading: Double,
        val distance: Double
    )
}