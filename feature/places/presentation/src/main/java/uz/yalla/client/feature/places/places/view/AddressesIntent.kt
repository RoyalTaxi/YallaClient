package uz.yalla.client.feature.places.places.view

import uz.yalla.client.core.domain.model.type.PlaceType


internal sealed interface AddressesIntent {
    data object OnNavigateBack : AddressesIntent
    data class OnAddNewAddress(val type: PlaceType) : AddressesIntent
    data class OnClickAddress(val id: Int, val type: PlaceType) : AddressesIntent
}