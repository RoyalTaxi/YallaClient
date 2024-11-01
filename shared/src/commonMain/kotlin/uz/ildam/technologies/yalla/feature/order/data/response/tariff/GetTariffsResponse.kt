package uz.ildam.technologies.yalla.feature.order.data.response.tariff

import kotlinx.serialization.Serializable

@Serializable
data class GetTariffsResponse(
    val map: String?,
    val tariff: List<Tariff>?
) {
    @Serializable
    data class Tariff(
        val category: Category?,
        val city_km_cost: Int?,
        val cost: Int?,
        val description: String?,
        val fixed_price: Int?,
        val fixed_type: Boolean?,
        val icon: String?,
        val id: Int?,
        val in_city_location: Boolean?,
        val included_km: Double?,
        val index: Int?,
        val min_out_city_cost: Int?,
        val modification: String?,
        val name: String?,
        val out_city_km_cost: Int?,
        val photo: String?,
        val second_address: Boolean?,
        val services: List<Service>?
    ) {
        @Serializable
        data class Category(
            val id: Int?,
            val name: String?
        )

        @Serializable
        data class Service(
            val cost: Int?,
            val cost_type: String?,
            val id: Int?,
            val name: String?,
        )
    }
}