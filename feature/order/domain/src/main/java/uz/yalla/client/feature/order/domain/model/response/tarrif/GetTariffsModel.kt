package uz.yalla.client.feature.order.domain.model.response.tarrif

import uz.yalla.client.core.domain.model.ServiceModel

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
        val cost: Int,
        val description: String,
        val fixedPrice: Int,
        val fixedType: Boolean,
        val icon: String,
        val id: Int,
        val index: Int,
        val name: String,
        val photo: String,
        val isSecondAddressMandatory: Boolean,
        val services: List<ServiceModel>
    ) {
        data class Category(
            val id: Int,
            val name: String
        )
    }
}