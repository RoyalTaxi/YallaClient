package uz.yalla.client.core.domain.model

import uz.yalla.client.core.domain.model.type.PlaceType


data class SearchableAddress(
    val addressId: Int?,
    val addressName: String,
    val distance: Double?,
    val type: PlaceType,
    val lat: Double,
    val lng: Double,
    val name: String
)