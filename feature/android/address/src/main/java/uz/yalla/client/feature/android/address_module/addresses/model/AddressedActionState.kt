package uz.yalla.client.feature.android.address_module.addresses.model

sealed interface AddressesActionState {
    data object Loading : AddressesActionState
    data object Error : AddressesActionState
    data object Success : AddressesActionState
}