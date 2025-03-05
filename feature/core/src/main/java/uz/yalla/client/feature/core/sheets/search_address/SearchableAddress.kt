package uz.yalla.client.feature.core.sheets.search_address

import uz.ildam.technologies.yalla.feature.addresses.domain.model.response.AddressType

data class SearchableAddress(
    val addressId: Int?,
    val addressName: String,
    val distance: Double?,
    val type: AddressType,
    val lat: Double,
    val lng: Double,
    val name: String
)