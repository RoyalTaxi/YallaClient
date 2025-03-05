package uz.yalla.client.service.order.request.order

import kotlinx.serialization.Serializable

@Serializable
data class OrderTaxiRequest(
    val dont_call_me: Boolean,
    val service: String,
    val card_id: String?,
    val address_id: Int,
    val to_phone: String,
    val comment: String,
    val tariff_id: Int,
    val tariff_options: List<Int>,
    val payment_type: String,
    val fixed_price: Boolean,
    val addresses: List<Address>,
) {
    @Serializable
    data class Address(
        val address_id: Int?,
        val lat: Double,
        val lng: Double,
        val name: String
    )
}