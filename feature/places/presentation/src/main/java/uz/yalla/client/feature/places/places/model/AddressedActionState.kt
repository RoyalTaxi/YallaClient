package uz.yalla.client.feature.places.places.model

internal sealed interface AddressesActionState {
    data object Loading : AddressesActionState
    data object Error : AddressesActionState
    data object Success : AddressesActionState
}