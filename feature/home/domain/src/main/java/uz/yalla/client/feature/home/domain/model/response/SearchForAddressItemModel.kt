package uz.yalla.client.feature.home.domain.model.response

data class SearchForAddressItemModel(
    val addressId: Int,
    val addressName: String,
    val db: Boolean,
    val distance: Double,
    val lat: Double,
    val lng: Double,
    val name: String
)