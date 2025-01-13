package uz.yalla.client.feature.android.address_module.address.model

internal sealed interface AddressActionState {
    data object Loading : AddressActionState
    data class Error(val errorMessage: String) : AddressActionState
    data object GetSuccess : AddressActionState
    data object PutSuccess : AddressActionState
    data object DeleteSuccess : AddressActionState
}