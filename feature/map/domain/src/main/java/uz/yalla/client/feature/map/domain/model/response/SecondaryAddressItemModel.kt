package uz.yalla.client.feature.map.domain.model.response

import uz.yalla.client.core.domain.model.type.PlaceType

data class SecondaryAddressItemModel(
    val distance: Double,
    val lat: Double,
    val lng: Double,
    val addressName: String,
    val name: String,
    val type: PlaceType
)