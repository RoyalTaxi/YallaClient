package uz.yalla.client.service.order.response.tariff

import kotlinx.serialization.Serializable
import uz.yalla.client.core.service.model.ServiceRemoteModel

@Serializable
data class GetTariffsResponse(
    val map: Map?,
    val tariff: List<Tariff?>?
) {
    @Serializable
    data class Map(
        val distance: Double?,
        val duration: Double?,
        val routing: List<Routing>?,
    ) {
        @Serializable
        data class Routing(
            val lat: Double?,
            val lng: Double?
        )
    }

    @Serializable
    data class Tariff(
        val category: Category?,
        val cost: Int?,
        val award: Award?,
        val description: String?,
        val fixed_price: Int?,
        val fixed_type: Boolean?,
        val icon: String?,
        val id: Int?,
        val index: Int?,
        val name: String?,
        val photo: String?,
        val second_address: Boolean?,
        val services: List<ServiceRemoteModel>?
    ) {
        @Serializable
        data class Category(
            val id: Int?,
            val name: String?
        )

        @Serializable
        data class Award(
            val cash_or_percentage: String?,
            val value: Int?
        )
    }
}