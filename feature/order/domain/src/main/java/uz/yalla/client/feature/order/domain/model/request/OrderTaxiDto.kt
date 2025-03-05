package uz.yalla.client.feature.order.domain.model.request

data class OrderTaxiDto(
    val dontCallMe: Boolean,
    val service: String,
    val cardId: String?,
    val addressId: Int,
    val toPhone: String,
    val comment: String,
    val tariffId: Int,
    val tariffOptions: List<Int>,
    val paymentType: String,
    val fixedPrice: Boolean,
    val addresses: List<Address>,
) {
    data class Address(
        val addressId: Int? = null,
        val lat: Double,
        val lng: Double,
        val name: String
    )
}