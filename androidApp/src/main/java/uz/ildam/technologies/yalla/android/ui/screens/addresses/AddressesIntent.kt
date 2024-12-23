package uz.ildam.technologies.yalla.android.ui.screens.addresses

import uz.ildam.technologies.yalla.feature.addresses.domain.model.response.AddressType

sealed interface AddressesIntent {
    data object OnNavigateBack : AddressesIntent
    data class OnAddNewAddress(val type: AddressType) : AddressesIntent
    data class OnClickAddress(val id: Int, val type: AddressType) : AddressesIntent
}