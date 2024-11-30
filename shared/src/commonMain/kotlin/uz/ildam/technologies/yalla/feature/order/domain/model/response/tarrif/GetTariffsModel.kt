package uz.ildam.technologies.yalla.feature.order.domain.model.response.tarrif

data class GetTariffsModel(
    val map: Map,
    val tariff: List<Tariff>
) {
    data class Map(
        val distance: Double,
        val duration: Double,
        val routing: List<Routing>,
    ) {
        data class Routing(
            val lat: Double,
            val lng: Double
        )
    }
    data class Tariff(
        val category: Category,
        val cityKmCost: Int,
        val cost: Int,
        val description: String,
        val fixedPrice: Int,
        val fixedType: Boolean,
        val icon: String,
        val id: Int,
        val inCityLocation: Boolean,
        val includedKm: Double,
        val index: Int,
        val minOutCityCost: Int,
        val modification: String,
        val name: String,
        val outCityKmCost: Int,
        val photo: String,
        val secondAddress: Boolean,
        val services: List<Service>
    ) {
        data class Category(
            val id: Int,
            val name: String
        )

        data class Service(
            val cost: Int,
            val costType: String,
            val id: Int,
            val name: String,
        )
    }
}