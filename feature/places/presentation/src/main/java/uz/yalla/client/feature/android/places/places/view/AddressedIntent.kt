package uz.yalla.client.feature.android.places.places.view

import uz.ildam.technologies.yalla.feature.addresses.domain.model.response.AddressType

internal sealed interface AddressesIntent {
    data object OnNavigateBack : AddressesIntent
    data class OnAddNewAddress(val type: AddressType) : AddressesIntent
    data class OnClickAddress(val id: Int, val type: AddressType) : AddressesIntent
}