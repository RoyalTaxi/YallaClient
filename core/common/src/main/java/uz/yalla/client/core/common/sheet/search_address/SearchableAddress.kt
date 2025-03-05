package uz.yalla.client.core.common.sheet.search_address

import uz.yalla.client.core.domain.model.PlaceType


data class SearchableAddress(
    val addressId: Int?,
    val addressName: String,
    val distance: Double?,
    val type: PlaceType,
    val lat: Double,
    val lng: Double,
    val name: String
)